package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class BaseSectionSave{
    public String name;
    public BlockPos origin;
    public BlockRotation rotation;
    public boolean isCore;

    public BaseSectionSave(String name, BlockPos origin, BlockRotation rotation, boolean isCore){
        this.name = name;
        this.origin = origin;
        this.rotation = rotation;
        this.isCore = isCore;
    }
}
