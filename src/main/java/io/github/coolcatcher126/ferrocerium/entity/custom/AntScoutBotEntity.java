package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;

public class AntScoutBotEntity extends HostileEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public AntScoutBotEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
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
            boolean invasionStart = true;//TODO: change invasionStart to check if invasion has started
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
            boolean invasionStart = true;//TODO: change invasionStart to check if invasion has started
            return invasionStart && super.canStart();
        }
    }

}
