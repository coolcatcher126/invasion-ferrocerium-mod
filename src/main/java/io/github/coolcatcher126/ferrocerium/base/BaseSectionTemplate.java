package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.world.World;

import java.util.ArrayList;

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
}
