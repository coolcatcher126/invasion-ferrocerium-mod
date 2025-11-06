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

    public boolean isWantedBlock(World world) {
        BlockState bs;
        if (world.isClient())
            bs = MinecraftClient.getInstance().world.getBlockState(this.blockPos);
        else
            bs = world.getBlockState(this.blockPos);

        boolean isMatchingWallBlock = this.blockState.getBlock() instanceof WallBlock && bs.getBlock() == this.blockState.getBlock();

        Block desiredBlock = this.blockState.getBlock();
        Block worldBlock = bs.getBlock();
        if ((desiredBlock instanceof StairsBlock && worldBlock instanceof StairsBlock) ||
                (desiredBlock instanceof FenceBlock && worldBlock instanceof FenceBlock) ||
                (desiredBlock instanceof WallBlock && worldBlock instanceof WallBlock) ||
                (desiredBlock instanceof PaneBlock && worldBlock instanceof PaneBlock))
            return true;

        return !this.blockState.isAir() && (bs == this.blockState || isMatchingWallBlock);
    }
}
