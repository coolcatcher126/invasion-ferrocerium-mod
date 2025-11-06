package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BaseSectPos extends Vec3i {
    private static final int SECTION_SIZE = 10;

    public BaseSectPos(int x, int y, int z) {super(x,y,z);}

    public BlockPos toBlockPos(){
        return new BlockPos(getX() * SECTION_SIZE, getY() * SECTION_SIZE, getZ() * SECTION_SIZE);
    }

    public BaseSectPos add(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BaseSectPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }
}
