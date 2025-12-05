package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.goal.AlienBotTargetGoal;
import io.github.coolcatcher126.ferrocerium.entity.goal.AlienBuilderGatherResourcesGoal;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.ListIterator;
import java.util.UUID;

public class AlienBuilderBotEntity extends HostileEntity implements InvasionBotEntity, InventoryOwner {
    private static final TrackedData<Boolean> BUILDING = DataTracker.registerData(AlienBuilderBotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> GATHERING = DataTracker.registerData(AlienBuilderBotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable
    private BaseSection sectionToBuild;
    @Nullable
    private AlienBase alienBase;
    private final SimpleInventory inventory = new SimpleInventory(9);

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
        if (alienBase != null){
            nbt.putUuid("alien_base", alienBase.getUuid());
            if (sectionToBuild != null){
                nbt.putInt("section_to_build", alienBase.getSections().indexOf(sectionToBuild));
            }
        }
        this.writeInventory(nbt, this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("alien_base")){
            UUID alienBaseUuid = nbt.getUuid("alien_base");
            alienBase = (InvasionFerroceriumComponents.getAlienBases(getEntityWorld())).stream()
                .filter(base -> alienBaseUuid.equals(base.getUuid()))
                .findAny()
                .orElse(null);
            if (alienBase == null){
                InvasionFerrocerium.LOGGER.info("No base is associated with the UUID %s".formatted(alienBaseUuid.toString()));
            }
            else{
                InvasionFerrocerium.LOGGER.info("Found a base associated with the UUID %s".formatted(alienBaseUuid.toString()));
                if (nbt.contains("section_to_build")){
                    sectionToBuild = alienBase.getSections().get(nbt.getInt("section_to_build"));
                }
            }
        }
        this.readInventory(nbt, this.getRegistryManager());
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new AlienBuilderAttackGoal(this, (double)1.0F, false));
        this.goalSelector.add(4, new AlienBuilderBuildGoal(this));
        this.goalSelector.add(5, new AlienBuilderGatherResourcesGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new AlienBotTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new AlienBotTargetGoal<>(this, LivingEntity.class, true, (e) -> !(e instanceof InvasionBotEntity)));
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
    protected SoundEvent getAmbientSound() {
        return ModSounds.ANT_SCOUT_BOT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ANT_SCOUT_BOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ANT_SCOUT_BOT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.ANT_SCOUT_BOT_STEP, 0.15F, 1.0F);
    }

    public double getBlockInteractionRange() {
        return this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    }

    public void setBuilding(boolean building)
    {
        this.dataTracker.set(BUILDING, building);
    }

    public boolean isBuilding()
    {
        return this.dataTracker.get(BUILDING);
    }

    public void setGathering(boolean gathering)
    {
        this.dataTracker.set(GATHERING, gathering);
    }

    public boolean isGathering()
    {
        return this.dataTracker.get(GATHERING);
    }

    public void setSection(BaseSection sectionToBuild){
        this.sectionToBuild = sectionToBuild;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BUILDING, false);
        builder.add(GATHERING, false);
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

    protected ItemStack addItem(ItemStack stack) {
        return this.inventory.addStack(stack);
    }

    protected boolean canInsertIntoInventory(ItemStack stack) {
        return this.inventory.canInsert(stack);
    }


    static class TargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        public TargetGoal(AlienBuilderBotEntity antSoldierBot, Class<T> targetEntityClass) {
            super(antSoldierBot, targetEntityClass, true);
        }

        @Override
        public boolean canStart() {
            boolean invasionStart = true;//TODO: change invasionStart to check if invasion has started
            return invasionStart && super.canStart();
        }
    }

    /// Check if the base section to build exists. If it does, check to see if it is complete. If not, build the section.
    public class AlienBuilderBuildGoal extends Goal {
        private final AlienBuilderBotEntity alienBuilderBot;
        private ListIterator<BaseBlock> blocks;
        private int delay;
        private Path path;
        BlockPos sectToBuildPos;

        AlienBuilderBuildGoal(AlienBuilderBotEntity alienBuilderBot){
            this.alienBuilderBot = alienBuilderBot;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (alienBase == null || sectionToBuild == null){
                return false;
            }
            else{
                this.sectToBuildPos = alienBase.getOrigin().add(sectionToBuild.getOrigin().toBlockPos());
                this.path = this.alienBuilderBot.getNavigation().findPathTo(this.sectToBuildPos, 0);
                return !sectionToBuild.isBuilt() && (this.path != null || this.alienBuilderBot.getAttackBox().contains(sectToBuildPos.toCenterPos()));
            }
        }

        @Override
        public boolean shouldContinue() {
            return (alienBase != null && sectionToBuild != null && blocks.hasNext() && this.alienBuilderBot.isInWalkTargetRange(sectToBuildPos));
        }

        public void tick(){
            assert alienBase != null;

            this.alienBuilderBot.getLookControl().lookAt(sectToBuildPos.toCenterPos());
            if (delay == 0){
                this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.alienBuilderBot.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                World world = getEntityWorld();
                if (blocks.hasNext() ) {
                    BaseBlock block = blocks.next();
                    if (!block.isWantedBlock(world)){
                        BlockPos blockPos = block.getBlockPos().add(alienBase.getOrigin());
                        BlockState blockState = block.getBlockState();

                        world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
                        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.alienBuilderBot, blockState));
                    }
                }
                delay = 5;
            }
            else {
                delay--;
            }
        }

        @Override
        public void start() {
            this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.alienBuilderBot.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            this.alienBuilderBot.setBuilding(true);
            assert this.alienBuilderBot.sectionToBuild != null;
            blocks = this.alienBuilderBot.sectionToBuild.getBaseBlockData().listIterator();
            delay = 0;
        }

        @Override
        public void stop() {
            this.alienBuilderBot.setBuilding(false);
            this.alienBuilderBot.sectionToBuild = null;
            this.alienBuilderBot.getNavigation().stop();
        }
    }

    public class AlienBuilderAttackGoal extends MeleeAttackGoal {
        private final AlienBuilderBotEntity alienBuilderBot;
        private int ticks;

        public AlienBuilderAttackGoal(AlienBuilderBotEntity alienBuilderBot, double speed, boolean pauseWhenMobIdle) {
            super(alienBuilderBot, speed, pauseWhenMobIdle);
            this.alienBuilderBot = alienBuilderBot;
        }

        public void start() {
            super.start();
            this.ticks = 0;
        }

        public void stop() {
            super.stop();
            this.alienBuilderBot.setAttacking(false);
        }

        public void tick() {
            super.tick();
            ++this.ticks;
            if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
                this.alienBuilderBot.setAttacking(true);
            } else {
                this.alienBuilderBot.setAttacking(false);
            }

        }
    }
    
}
