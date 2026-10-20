package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseBlocks {
    protected final World world;
    protected final BaseSectPos origin;
    protected ArrayList<BaseBlock> blocks;

    public BaseBlocks(World world, BaseSectPos origin){
        this.world = world;
        this.origin = origin;
    }

    public BaseBlocks(World world, BaseSectPos origin, ArrayList<BaseBlock> blocks){
        this.world = world;
        this.origin = origin;
        this.blocks = blocks;
    }

    /// Returns true when the blocks in the location of the base section matches the base section's template
    public boolean isBuilt(){
        for (BaseBlock block : blocks) {
            if (!(block.getBlockState().isAir() || block.isWantedBlock(world))){
                return false;
            }
        }
        return true;
    }

    public ArrayList<BaseBlock> getBlocks(){
        return this.blocks;
    }

    public Set<Item> getBaseBlockPallete(){
        return blocks.stream().map(x-> x.getBlockState().getBlock().asItem()).collect(Collectors.toSet());
    }

    public BaseSectPos getOrigin(){
        return this.origin;
    }
}
