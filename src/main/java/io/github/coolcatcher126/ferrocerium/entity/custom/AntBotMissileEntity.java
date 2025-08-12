package io.github.coolcatcher126.ferrocerium.entity.custom;

import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.QuaternionfInterpolator;

import java.util.UUID;

public class AntBotMissileEntity extends ProjectileEntity {
    @Nullable
    private Entity target;
    @Nullable
    private UUID targetUuid;
    double sensitivity = 0.125;

    public AntBotMissileEntity(EntityType<? extends AntBotMissileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AntBotMissileEntity(World world, LivingEntity owner, @Nullable Entity target, Direction direction) {
        this(ModEntities.ANT_BOT_MISSILE, world);
        this.setOwner(owner);
        Vec3d vec3d = owner.getBoundingBox().getCenter();
        this.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.getYaw(), this.getPitch());
        this.target = target;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.target != null) {
            nbt.putUuid("Target", this.target.getUuid());
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("Target")) {
            this.targetUuid = nbt.getUuid("Target");
        }
    }

    @Override
    public void checkDespawn() {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.target == null && this.targetUuid != null) {
                this.target = ((ServerWorld)this.getWorld()).getEntity(this.targetUuid);
                if (this.target == null) {
                    this.targetUuid = null;
                }
            }

            if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && this.target.isSpectator()) {
                this.applyGravity();
            } else {
                //Shuffle current direction towards target
                Vec3d currentDirection = this.getVelocity();
                if (this.target != null && !this.target.isRemoved()) {
                    //Point towards the target
                    Vec3d targetDirection = target.getEyePos().subtract(this.getEyePos());
                    currentDirection = targetDirection.subtract(currentDirection).normalize().multiply(this.sensitivity).add(currentDirection).normalize();
                }
                this.setVelocity(currentDirection.multiply(0.75));
            }

            //Allow deflection
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.hitOrDeflect(hitResult);
            }
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        ProjectileUtil.setRotationFromVelocity(this, 0.5F);
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX() - vec3d.x, this.getY() - vec3d.y, this.getZ() - vec3d.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 4);

        if (!this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }
}
