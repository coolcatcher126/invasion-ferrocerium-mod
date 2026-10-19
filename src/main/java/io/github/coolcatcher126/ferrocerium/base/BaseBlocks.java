package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseBlocks {
    protected final World world;
    protected ArrayList<BaseBlock> blocks;

    public BaseBlocks(World world){
        this.world = world;
    }

    public BaseBlocks(World world, ArrayList<BaseBlock> blocks){
        this.world = world;
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
        return new BaseSectPos(0,0,0);
    }
}
