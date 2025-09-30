package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AlienBuilderBotEntity extends HostileEntity {
    private static final TrackedData<Boolean> BUILDING = DataTracker.registerData(AlienBuilderBotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable
    private BaseSection sectionToBuild;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static int REQUIRED_INVASION_LEVEL = 3;

    public AlienBuilderBotEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new AlienBuilderAttackGoal(this, (double)1.0F, false));
        this.goalSelector.add(5, new AlienBuilderBuildGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new AlienBuilderBotEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new AlienBuilderBotEntity.TargetGoal<>(this, IronGolemEntity.class));
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

    private void setBuilding(boolean attacking)
    {
        this.dataTracker.set(BUILDING, attacking);
    }

    public boolean isBuilding()
    {
        return this.dataTracker.get(BUILDING);
    }

    public void setSection(BaseSection sectionToBuild){
        this.sectionToBuild = sectionToBuild;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BUILDING, false);
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

        AlienBuilderBuildGoal(AlienBuilderBotEntity alienBuilderBot){
            this.alienBuilderBot = alienBuilderBot;
        }

        @Override
        public boolean canStart() {
            boolean shouldBuild = sectionToBuild != null;
            if (shouldBuild)
            {
                shouldBuild = sectionToBuild.isBuilt();
            }
            return shouldBuild;
        }

        public void tick(){
            World world = this.alienBuilderBot.getWorld();
            assert this.alienBuilderBot.sectionToBuild != null;
            ArrayList<BaseBlock> blocks = this.alienBuilderBot.sectionToBuild.getBaseBlockData();
            for (BaseBlock block : blocks) {
                if (!block.isPlaced(world)){
                    BlockPos blockPos = block.getBlockPos();
                    BlockState blockState = block.getBlockState();
                    world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.alienBuilderBot, blockState));
                }
            }
        }

        @Override
        public void start() {
            this.alienBuilderBot.setBuilding(true);
        }

        @Override
        public void stop() {
            this.alienBuilderBot.setBuilding(false);
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
