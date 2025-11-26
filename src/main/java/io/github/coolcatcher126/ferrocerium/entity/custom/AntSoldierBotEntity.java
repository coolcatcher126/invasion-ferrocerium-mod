package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.goal.AlienBotTargetGoal;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AntSoldierBotEntity extends HostileEntity implements InvasionBotEntity {
    private static final TrackedData<Boolean> FIRING_ROCKETS = DataTracker.registerData(AntSoldierBotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public final AnimationState rangedAttackAnimationState = new AnimationState();
    private int rangedAttackAnimationTimeout = 0;

    private static int REQUIRED_INVASION_LEVEL = 2;

    public AntSoldierBotEntity(EntityType<? extends HostileEntity> entityType, World world) {
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
        this.goalSelector.add(2, new AntSoldierBotEntity.RocketAttackGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new AntSoldierBotEntity.AntSoldierBotAttackGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new AlienBotTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new AlienBotTargetGoal<>(this, LivingEntity.class, true, (e) -> !(e instanceof InvasionBotEntity)));
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

        if(this.isRangedAttacking() && rangedAttackAnimationTimeout <= 0) {
            rangedAttackAnimationTimeout = 40;
            rangedAttackAnimationState.start(this.age);
        } else {
            --this.rangedAttackAnimationTimeout;
        }

        if(!this.isRangedAttacking()) {
            rangedAttackAnimationState.stop();
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

    private void setRangedAttacking(boolean attacking)
    {
        this.dataTracker.set(FIRING_ROCKETS, attacking);
    }

    public boolean isRangedAttacking()
    {
        return this.dataTracker.get(FIRING_ROCKETS);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FIRING_ROCKETS, false);
    }

    //RocketAttackGoal: Fire a salvo of two rockets
    static class RocketAttackGoal extends Goal {
        private final AntSoldierBotEntity entity;
        private int rocketsFired;
        private int rocketCooldown;
        private int targetNotVisibleTicks;

        public RocketAttackGoal(AntSoldierBotEntity soldierBot) {
            this.entity = soldierBot;
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.entity.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.entity.canTarget(livingEntity);
        }

        @Override
        public void start() {
            this.rocketsFired = 0;
            this.entity.setRangedAttacking(true);
        }

        @Override
        public void stop() {
            this.targetNotVisibleTicks = 0;
            this.entity.setRangedAttacking(false);
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            this.rocketCooldown--;
            LivingEntity target = this.entity.getTarget();//Ensure target exists
            if (target != null)
            {
                boolean targetVisible = this.entity.getVisibilityCache().canSee(target);
                if (targetVisible) {
                    this.targetNotVisibleTicks = 0;
                } else {
                    this.targetNotVisibleTicks++;
                }

                double sqrDist = this.entity.squaredDistanceTo(target);
                //Meelee attack
                if (sqrDist < 4.0) {
                    if (!targetVisible) {
                        return;
                    }
                    else if (this.rocketCooldown <= 0) {
                        this.rocketCooldown = 20;
                        this.entity.tryAttack(target);
                    }
                }
                //If target is too far for meelee but in range of ranged and in line of sight: Ranged Attack.
                else if (sqrDist > this.getFollowRange() * this.getFollowRange() && sqrDist < (this.getFollowRange() + 10) * (this.getFollowRange() + 10) && targetVisible) {
                    if (this.rocketCooldown <= 0) {
                        this.rocketsFired++;

                        if (this.rocketsFired == 1) {
                            this.rocketCooldown = 60;
                        }
                        else if (this.rocketsFired <= 3) {
                            this.rocketCooldown = 6;
                        } else {
                            this.rocketCooldown = 120;
                            this.rocketsFired = 0;
                        }

                        if (this.rocketsFired > 1) {
                            double spread = Math.sqrt(Math.sqrt(sqrDist)) * 0.5;
                            if (!this.entity.isSilent()) {
                                this.entity.getWorld().syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, this.entity.getBlockPos(), 0);//Play rocket launch sound
                            }

                            double diffX = target.getX() - this.entity.getX();
                            double diffY = target.getBodyY(0.5) - this.entity.getBodyY(0.5);
                            double diffZ = target.getZ() - this.entity.getZ();

                            Vec3d vec3d = new Vec3d(this.entity.getRandom().nextTriangular(diffX, 2.297 * spread), diffY, this.entity.getRandom().nextTriangular(diffZ, 2.297 * spread));

                            AntBotMissileEntity rocketEntity = new AntBotMissileEntity(this.entity.getWorld(), this.entity, target, Direction.getFacing(this.entity.getRotationVector()), 1);
                            rocketEntity.setPosition(rocketEntity.getX(), this.entity.getBodyY(0.5) + 0.5, rocketEntity.getZ());
                            this.entity.getWorld().spawnEntity(rocketEntity);
                        }
                    }

                    this.entity.getLookControl().lookAt(target, 10.0F, 10.0F);
                } else if (this.targetNotVisibleTicks < 5) {
                    this.entity.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.0);
                }

                super.tick();
            }
        }

        private double getFollowRange() {
            return this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        }
    }

    static class AntSoldierBotAttackGoal extends MeleeAttackGoal {
        public AntSoldierBotAttackGoal(AntSoldierBotEntity antSoldierBot) {
            super(antSoldierBot, 1.0, true);
        }

        @Override
        public boolean shouldContinue() {
            boolean invasionStart = InvasionFerroceriumComponents.getInvasionLevel(super.mob.getEntityWorld()) > 0;
            if (invasionStart && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.shouldContinue();
            }
        }
    }

}
