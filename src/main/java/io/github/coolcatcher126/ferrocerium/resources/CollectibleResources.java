package io.github.coolcatcher126.ferrocerium.resources;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public class CollectibleResources {
    final List<Block> COLLECTIBLE_WOOD_BLOCKS = List.of(
            Blocks.ACACIA_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.CHERRY_LOG, Blocks.OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG,
            Blocks.ACACIA_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.CHERRY_PLANKS, Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.JUNGLE_PLANKS,
            Blocks.ACACIA_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.CHERRY_LEAVES, Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES
    );

    final List<Block> COLLECTIBLE_ORE_BLOCKS = List.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, ModBlocks.ALUMINUM_ORE_BLOCK, ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK
    );

    final  List<Block> COLLECTIBLE_BLOCKS = Stream.concat(COLLECTIBLE_WOOD_BLOCKS.stream(), Stream.concat(Stream.of(Blocks.STONE), COLLECTIBLE_ORE_BLOCKS.stream())).toList();

    public boolean blockIsCollectible(World world, BlockPos blockPos){
        return COLLECTIBLE_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
    }

    public boolean blockIsCollectible(World world, BlockPos blockPos, EnumSet<ResourceCategory> resCat){
        boolean collectible = false;
        if (resCat.contains(ResourceCategory.ORES)) {
            collectible |= COLLECTIBLE_ORE_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
        }
        if (resCat.contains(ResourceCategory.WOOD)) {
            collectible |= COLLECTIBLE_WOOD_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
        }
        if (resCat.contains(ResourceCategory.STONE)) {
            collectible |= Blocks.STONE == world.getBlockState(blockPos).getBlock();
        }

        return collectible;
    }
}
