package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;

public class AntScoutBotEntity extends HostileEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    
    private static int REQUIRED_INVASION_LEVEL = 1;

    public AntScoutBotEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static boolean isSpawnDark(ServerWorldAccess world, BlockPos pos, Random random) {
        if (REQUIRED_INVASION_LEVEL >= world.toServerWorld().getComponent(InvasionFerroceriumComponents.INVASION_LEVEL).getInvasionState() || world.getLightLevel(LightType.SKY, pos) > random.nextInt(32)) {
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
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new AntScoutBotEntity.AttackGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new AntScoutBotEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new AntScoutBotEntity.TargetGoal<>(this, IronGolemEntity.class));
    }

    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0)
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

    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(AntScoutBotEntity antScoutBot) {
            super(antScoutBot, 1.0, true);
        }

        @Override
        public boolean shouldContinue() {
            boolean invasionStart = InvasionFerroceriumComponents.getInvasionLevel(this.mob.getEntityWorld()) > 1;
            if (invasionStart && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.shouldContinue();
            }
        }
    }

    static class TargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        public TargetGoal(AntScoutBotEntity antScoutBot, Class<T> targetEntityClass) {
            super(antScoutBot, targetEntityClass, true);
        }

        @Override
        public boolean canStart() {
            boolean invasionStart = InvasionFerroceriumComponents.getInvasionLevel(this.mob.getEntityWorld()) > 1;
            return invasionStart && super.canStart();
        }
    }

}
