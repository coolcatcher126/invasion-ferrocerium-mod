package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;

import java.util.EnumSet;

public class AlienHelicopterBotEntity extends FlyingEntity implements Monster, InvasionBotEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static int REQUIRED_INVASION_LEVEL = 3;

    public AlienHelicopterBotEntity(EntityType<? extends AlienHelicopterBotEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new HelicopterMoveControl(this);
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

    public static boolean canSpawnInDark(EntityType<? extends AlienHelicopterBotEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (SpawnReason.isTrialSpawner(spawnReason) || isSpawnDark(world, pos, random)) && canMobSpawn(type, world, spawnReason, pos, random);
    }

    public static boolean canSpawnIgnoreLightLevel(EntityType<? extends AlienHelicopterBotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && canMobSpawn(type, world, spawnReason, pos, random);
    }

    protected void initGoals() {
        this.goalSelector.add(4, new RocketAttackGoal(this));
        this.goalSelector.add(5, new FlyRandomlyGoal(this));
        this.goalSelector.add(7, new LookAtTargetGoal(this));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.add(3, new TargetGoal<>(this, IronGolemEntity.class));
    }

    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D);
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ANT_SCOUT_BOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ANT_SCOUT_BOT_DEATH;
    }

    static class HelicopterMoveControl extends MoveControl {
        private final AlienHelicopterBotEntity entity;
        private int collisionCheckCooldown;

        public HelicopterMoveControl(AlienHelicopterBotEntity helicopterBot) {
            super(helicopterBot);
            this.entity = helicopterBot;
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                if (this.collisionCheckCooldown-- <= 0) {
                    this.collisionCheckCooldown += this.entity.getRandom().nextInt(5) + 2;
                    Vec3d vec3d = new Vec3d(this.targetX - this.entity.getX(), this.targetY - this.entity.getY(), this.targetZ - this.entity.getZ());
                    double d = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                        this.entity.setVelocity(this.entity.getVelocity().add(vec3d.multiply(0.1)));
                    } else {
                        this.state = State.WAIT;
                    }
                }

            }
        }

        private boolean willCollide(Vec3d direction, int steps) {
            Box box = this.entity.getBoundingBox();

            for(int i = 1; i < steps; ++i) {
                box = box.offset(direction);
                if (!this.entity.getWorld().isSpaceEmpty(this.entity, box)) {
                    return false;
                }
            }

            return true;
        }
    }

    //RocketAttackGoal: Fire a salvo of two rockets
    static class RocketAttackGoal extends Goal {
        private final AlienHelicopterBotEntity entity;
        private int rocketsFired;
        private int rocketCooldown;
        private int targetNotVisibleTicks;

        public RocketAttackGoal(AlienHelicopterBotEntity helicopterBot) {
            this.entity = helicopterBot;
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.entity.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.entity.canTarget(livingEntity);
        }

        @Override
        public void start() {
            this.rocketsFired = 0;
            this.rocketCooldown = 0;
        }

        @Override
        public void stop() {
            this.targetNotVisibleTicks = 0;
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

                //If target is in range of ranged and in line of sight: Ranged Attack.
                if (sqrDist < (this.getFollowRange() + 10) * (this.getFollowRange() + 10) && targetVisible) {
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
                }

                super.tick();
            }
        }

        private double getFollowRange() {
            return this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        }
    }

    static class FlyRandomlyGoal extends Goal {
        private final AlienHelicopterBotEntity entity;

        public FlyRandomlyGoal(AlienHelicopterBotEntity alienHelicopterBot) {
            this.entity = alienHelicopterBot;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            MoveControl moveControl = this.entity.getMoveControl();
            if (!moveControl.isMoving()) {
                return true;
            } else {
                double diffX = moveControl.getTargetX() - this.entity.getX();
                double diffY = moveControl.getTargetY() - this.entity.getY();
                double diffZ = moveControl.getTargetZ() - this.entity.getZ();
                double sqrDist = diffX * diffX + diffY * diffY + diffZ * diffZ;
                return sqrDist < (double)1.0F || sqrDist > (double)3600.0F;
            }
        }

        public boolean shouldContinue() {
            return false;
        }

        public void start() {
            Random random = this.entity.getRandom();
            double targetX = this.entity.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double targetY = this.entity.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double targetZ = this.entity.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.entity.getMoveControl().moveTo(targetX, targetY, targetZ, (double)1.0F);
        }
    }

    static class LookAtTargetGoal extends Goal {
        private final AlienHelicopterBotEntity entity;

        public LookAtTargetGoal(AlienHelicopterBotEntity alienHelicopterBot) {
            this.entity = alienHelicopterBot;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            return true;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            if (this.entity.getTarget() == null) {
                Vec3d vec3d = this.entity.getVelocity();
                this.entity.setYaw(-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180F / (float)Math.PI));
                this.entity.bodyYaw = this.entity.getYaw();
            } else {
                LivingEntity livingEntity = this.entity.getTarget();
                double d = (double)64.0F;
                if (livingEntity.squaredDistanceTo(this.entity) < (double)4096.0F) {
                    double e = livingEntity.getX() - this.entity.getX();
                    double f = livingEntity.getZ() - this.entity.getZ();
                    this.entity.setYaw(-((float)MathHelper.atan2(e, f)) * (180F / (float)Math.PI));
                    this.entity.bodyYaw = this.entity.getYaw();
                }
            }

        }
    }
    
    static class TargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        public TargetGoal(AlienHelicopterBotEntity alienHelicopterBot, Class<T> targetEntityClass) {
            super(alienHelicopterBot, targetEntityClass, true);
        }

        @Override
        public boolean canStart() {
            boolean invasionStart = true;//TODO: change invasionStart to check if invasion has started
            return invasionStart && super.canStart();
        }
    }
}
