package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BaseBlock {
    private BlockPos blockPos;
    private BlockState blockState;

    BaseBlock(BlockPos blockPos, BlockState blockState){
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public BlockPos getBlockPos() { return blockPos; }
    public BlockState getBlockState() { return blockState; }

    public void setBlockPos(BlockPos bp) { this.blockPos = bp; }
    public void setBlockState(BlockState bs) { this.blockState = bs; }

    public boolean isPlaced(World world) {
        BlockState bs;
        if (world.isClient())
            bs = MinecraftClient.getInstance().world.getBlockState(this.blockPos);
        else
            bs = world.getBlockState(this.blockPos);

        boolean isMatchingWallBlock = this.blockState.getBlock() instanceof WallBlock && bs.getBlock() == this.blockState.getBlock();

        Block block1 = this.blockState.getBlock();
        Block block2 = bs.getBlock();
        if ((block1 instanceof StairsBlock && block2 instanceof StairsBlock) ||
                (block1 instanceof FenceBlock && block2 instanceof FenceBlock) ||
                (block1 instanceof WallBlock && block2 instanceof WallBlock) ||
                (block1 instanceof PaneBlock && block2 instanceof PaneBlock))
            return true;

        return !this.blockState.isAir() && (bs == this.blockState || isMatchingWallBlock);
    }
}
