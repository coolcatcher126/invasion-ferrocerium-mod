package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;

// Registered object in BaseSections
// Defines the constant data shared between instances of the base piece.
public abstract class BaseSectionTemplate {
    public String name;
    public final boolean isCore;

    public BaseSectionTemplate(String name, boolean isCore){
        this.name = name;
        this.isCore = isCore;
    }

    public ArrayList<BaseBlock> getRelativeBlockData(World world) {
        return BaseDataHelper.getBaseBlocksFromNbt(this.name, world);
    }

    public Set<Item> getBlockPallete(World world){
        return BaseDataHelper.getBaseBlockPalleteFromNbt(this.name, world);
    }
}
