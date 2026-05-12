package io.github.coolcatcher126.ferrocerium.entity.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MinerNavigation extends EntityNavigation {

    public MinerNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.isOnGround() || this.entity.isInFluid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.getPathfindingY(), this.entity.getZ());
    }


    @Override
    public Path findPathTo(BlockPos target, int distance) {
        WorldChunk worldChunk = this.world
                .getChunkManager()
                .getWorldChunk(ChunkSectionPos.getSectionCoord(target.getX()), ChunkSectionPos.getSectionCoord(target.getZ()));
        if (worldChunk == null) {
            return null;
        } else {
            if (worldChunk.getBlockState(target).isAir()) {
                BlockPos blockPos = target.down();

                while (blockPos.getY() > this.world.getBottomY() && worldChunk.getBlockState(blockPos).isAir()) {
                    blockPos = blockPos.down();
                }

                if (blockPos.getY() > this.world.getBottomY()) {
                    return super.findPathTo(blockPos.up(), distance);
                }

                while (blockPos.getY() < this.world.getTopY() && worldChunk.getBlockState(blockPos).isAir()) {
                    blockPos = blockPos.up();
                }

                target = blockPos;
            }

            return super.findPathTo(target, distance);
        }
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getBlockPos(), distance);
    }

    private int getPathfindingY() {
        if (this.entity.isTouchingWater() && this.canSwim()) {
            int i = this.entity.getBlockY();
            BlockState blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), i, this.entity.getZ()));
            int j = 0;

            while (blockState.isOf(Blocks.WATER)) {
                blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), ++i, this.entity.getZ()));
                if (++j > 16) {
                    return this.entity.getBlockY();
                }
            }

            return i;
        } else {
            return MathHelper.floor(this.entity.getY() + 0.5);
        }
    }
}
