package io.github.coolcatcher126.ferrocerium.entity.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModSensorType;
import io.github.coolcatcher126.ferrocerium.entity.ai.pathing.MinerNavigation;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlienBuilderBotEntity extends HostileEntity implements InvasionBotEntity, InventoryOwner {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super AlienBuilderBotEntity>>> SENSORS = ImmutableList.of(
            ModSensorType.NEAREST_CHEST, SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );

    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.NEAREST_ATTACKABLE,
            ModMemoryModuleTypes.BASE_SECTION_LOCATION,
            ModMemoryModuleTypes.RESOURCE_LOCATION,
            ModMemoryModuleTypes.BUILDING,
            ModMemoryModuleTypes.EXCHANGING,
            ModMemoryModuleTypes.GATHERING,
            ModMemoryModuleTypes.MINING,
            ModMemoryModuleTypes.ACTIVITY_TICKS
    );

    @Nullable
    private BaseSection sectionToBuild;
    @Nullable
    private Vein vein;
    @Nullable
    private AlienBase alienBase;
    private final SimpleInventory inventory = new SimpleInventory(9);
    private final List<ItemStack> unwantedItems = new ArrayList<>();
    private final List<ItemVariant> wantedItems = new ArrayList<>();
    public final InventoryStorage inventoryWrapper = InventoryStorage.of(inventory, null);
    private List<Item> itemsToCraft = new ArrayList<>();

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static int REQUIRED_INVASION_LEVEL = 3;

    /// Natural spawn constructor.
    /// Creates a base.
    public AlienBuilderBotEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    /// Base helper spawn.
    /// Add to a preexisting base.
    public AlienBuilderBotEntity(World world, AlienBase base){
        super(ModEntities.ALIEN_BUILDER_BOT, world);
        this.setCanPickUpLoot(true);
        this.alienBase = base;
    }

    public static boolean isSpawnDark(ServerWorldAccess world, BlockPos pos, Random random) {
        if (REQUIRED_INVASION_LEVEL > world.toServerWorld().getComponent(InvasionFerroceriumComponents.INVASION_LEVEL).getInvasionState() || world.getLightLevel(LightType.SKY, pos) > random.nextInt(32)) {
            return false;
        } else {
            DimensionType dimensionType = world.getDimension();
            int i = dimensionType.monsterSpawnBlockLightLimit();
            if (i < 15 && world.getLightLevel(LightType.BLOCK, pos) > i) {
                return false;
            } else {
                int j = world.toServerWorld().isThundering() ? world.getLightLevel(pos, 10) : world.getLightLevel(pos);
                return j <= dimensionType.monsterSpawnLightTest().get(random);
            }
        }
    }

    public static boolean canSpawnInDark(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (SpawnReason.isTrialSpawner(spawnReason) || isSpawnDark(world, pos, random)) && canMobSpawn(type, world, spawnReason, pos, random);
    }

    public static boolean canSpawnIgnoreLightLevel(EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && canMobSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        if (vein != null && vein.size() > 0) {
            nbt.put("vein", Vein.writeToNbt(vein));
        }

        if (itemsToCraft != null && !itemsToCraft.isEmpty()){
            NbtCompound nbtCompound;
            NbtList nbtList = new NbtList();
            for (Item item : itemsToCraft) {
                nbtCompound = new NbtCompound();
                nbtCompound.putInt("item", Item.getRawId(item));
                nbtList.add(nbtCompound);
            }
            nbt.put("crafting_list", nbtList);
        }

        if (alienBase != null) {
            nbt.putUuid("alien_base", alienBase.getUuid());
            if (sectionToBuild != null) {
                nbt.putInt("section_to_build", alienBase.getSections().indexOf(sectionToBuild));
            }
        }

        this.writeInventory(nbt, this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        NbtList nbtList = nbt.getList("crafting_list", NbtElement.COMPOUND_TYPE);
        int item;
        for (NbtElement nbtElement : nbtList){
            if (nbtElement instanceof NbtCompound){
                item = ((NbtCompound) nbtElement).getInt("item");
                addCraftingRequest(Item.byRawId(item));
            }
            else{
                throw new InvalidNbtException("item data does not exist");
            }
        }

        if(nbt.contains("alien_base")){
            UUID alienBaseUuid = nbt.getUuid("alien_base");
            alienBase = (InvasionFerroceriumComponents.getAlienBases(getEntityWorld())).stream()
                .filter(base -> alienBaseUuid.equals(base.getUuid()))
                .findAny()
                .orElse(null);
            if (alienBase != null) {
                //InvasionFerrocerium.LOGGER.info("Found a base associated with the UUID %s".formatted(alienBaseUuid.toString()));
                alienBase.hireBuilder(this);
                if (nbt.contains("section_to_build")){
                    setSection(alienBase.getSections().get(nbt.getInt("section_to_build")));
                }
            } else {
                InvasionFerrocerium.LOGGER.info("No base is associated with the UUID %s".formatted(alienBaseUuid.toString()));
            }
        }

        if (nbt.contains("vein")) {
            setVein(Vein.readfromNbt(nbt.getCompound("vein")));
        }

        this.readInventory(nbt, this.getRegistryManager());
    }


    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0F);
    }

    private void setupAnimationStates(){
        if (this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        }
        else{
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getEntityWorld().isClient()){
            this.setupAnimationStates();
        }

        else if (this.alienBase == null && this.age >= 20){
            this.alienBase = new AlienBase(this.getEntityWorld(), this.getBlockPos(), this);
            InvasionFerroceriumComponents.addAlienBase(this.getEntityWorld(), this.alienBase);
        }
    }

    @Override
    protected void mobTick(){
        this.getWorld().getProfiler().push("alienBuilderBotBrain");
        this.getBrain().tick((ServerWorld)this.getWorld(), this);
        this.getWorld().getProfiler().pop();
        this.getWorld().getProfiler().push("AlienBuilderBotUpdate");
        AlienBuilderBotBrain.updateActivities(this);
        this.getWorld().getProfiler().pop();
        super.mobTick();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ALIEN_BUILDER_BOT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ALIEN_BUILDER_BOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ALIEN_BUILDER_BOT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.ALIEN_BUILDER_BOT_STEP, 0.15F, 1.0F);
    }

    public double getBlockInteractionRange() {
        return this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    }

    public AlienBase getBase(){
        return this.alienBase;
    }

    public void setBuilding(boolean building)
    {
        this.brain.remember(ModMemoryModuleTypes.BUILDING, building);
        this.brain.remember(ModMemoryModuleTypes.ACTIVITY_TICKS, building ? 600:null);
    }

    public boolean isBuilding()
    {
        return this.brain.getOptionalRegisteredMemory(ModMemoryModuleTypes.BUILDING).orElse(false);
    }

    public void setGathering(boolean gathering)
    {
        this.brain.remember(ModMemoryModuleTypes.GATHERING, gathering);
        this.brain.remember(ModMemoryModuleTypes.ACTIVITY_TICKS, gathering ? 600 : null);
    }

    public boolean isExchanging()
    {
        return this.brain.getOptionalRegisteredMemory(ModMemoryModuleTypes.EXCHANGING).orElse(false);
    }

    public void setExchanging(boolean exchanging)
    {
        this.brain.remember(ModMemoryModuleTypes.EXCHANGING, exchanging);
        this.brain.remember(ModMemoryModuleTypes.ACTIVITY_TICKS, exchanging ? 600 : null);
    }

    public boolean isGathering()
    {
        return this.brain.getOptionalRegisteredMemory(ModMemoryModuleTypes.GATHERING).orElse(false);
    }

    public void setMining(boolean mining)
    {
        this.brain.remember(ModMemoryModuleTypes.MINING, mining);
        this.brain.remember(ModMemoryModuleTypes.ACTIVITY_TICKS, mining ? 600 : null);
    }

    public boolean isMining()
    {
        return this.brain.getOptionalRegisteredMemory(ModMemoryModuleTypes.MINING).orElse(false);
    }

    public void setSection(BaseSection sectionToBuild){
        setSection(sectionToBuild, this.alienBase);
    }

    public void setSection(BaseSection sectionToBuild, AlienBase alienBase){
        this.sectionToBuild = sectionToBuild;
        if (alienBase != null && sectionToBuild != null) {
            this.brain.remember(ModMemoryModuleTypes.BASE_SECTION_LOCATION, new GlobalPos(alienBase.getDimension(), sectionToBuild.getOrigin().toBlockPos().add(alienBase.getOrigin())));
        }
        else {
            this.brain.forget(ModMemoryModuleTypes.BASE_SECTION_LOCATION);
        }
    }

    public BaseSection getSection(){
        return this.sectionToBuild;
    }

    public void setVein(@Nullable Vein vein){
        if (vein.size() == 0){
            vein = null;
        }
        this.vein = vein;
        if (vein != null) {
            if (alienBase != null) {
                this.brain.remember(ModMemoryModuleTypes.RESOURCE_LOCATION, new GlobalPos(alienBase.getDimension(), vein.getClosest(this.getBlockPos())));
            }
        }
        else {
            this.brain.forget(ModMemoryModuleTypes.RESOURCE_LOCATION);
        }
    }

    public @Nullable Vein getVein(){
        return this.vein;
    }


    @Override
    public boolean cannotDespawn() {
        //Disallow despawning when assigned to a base.
        return super.cannotDespawn() || alienBase != null;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public void addCraftingRequest(Item requestedItem, int count){
        for (int i = 0; i < count; i++) {
            this.itemsToCraft.add(requestedItem);
        }
    }

    public void addCraftingRequest(Item requestedItem){
        this.itemsToCraft.add(requestedItem);
    }

    public void setItemsToCraft(List<Item> items){
        this.itemsToCraft = items;
    }

    public List<Item> getItemsToCraft(){
        return this.itemsToCraft;
    }

    public List<ItemStack> getUnwantedItems(){
        return unwantedItems;
    }

    public List<ItemVariant> getWantedItems(){
        return wantedItems;
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.inventory.canInsert(itemStack)){
            this.triggerItemPickedUpByEntityCriteria(item);
            ItemStack itemStack2 = this.inventory.addStack(itemStack);
            if (itemStack2.isEmpty()){
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }
        else {
            super.loot(item);
        }
    }

    @Override
    public void readInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        InventoryOwner.super.readInventory(nbt, wrapperLookup);
    }

    @Override
    public void writeInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        InventoryOwner.super.writeInventory(nbt, wrapperLookup);
    }

    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
        super.dropEquipment(world, source, causedByPlayer);
        this.inventory.clearToList().forEach(this::dropStack);
    }

    public ItemStack addItem(ItemStack stack) {
        return this.inventory.addStack(stack);
    }

    public boolean canInsertIntoInventory(ItemStack stack) {
        return this.inventory.canInsert(stack);
    }

    @Override
    protected Brain.Profile<AlienBuilderBotEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return AlienBuilderBotBrain.create(this, this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    public Brain<AlienBuilderBotEntity> getBrain() {
        return (Brain<AlienBuilderBotEntity>) super.getBrain();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MinerNavigation(this, world);
    }

    @Override
    public int getMaxLookPitchChange() {
        return 170;
    }
}
