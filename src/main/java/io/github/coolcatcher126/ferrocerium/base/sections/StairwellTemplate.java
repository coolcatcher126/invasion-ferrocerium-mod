package io.github.coolcatcher126.ferrocerium.base.sections;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseSectionTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class StairwellTemplate extends BaseSectionTemplate {
    public final static String name = "stairwell";

    public StairwellTemplate() {
        super(name, true);
    }

    @Override
    public ArrayList<BaseBlock> getRelativeBlockData(World world) {
        ArrayList<BaseBlock> steps = new ArrayList<>();
        BlockState bs = Blocks.STONE_BRICKS.getDefaultState();
        for (int y = 60, i = 0; y >= -53; y--, i++) {
            int step = i % 8;
            if (step != 7) {
                steps.add(new BaseBlock(new BlockPos(-1, -i, -1), bs));
            }
            if (step != 6) {
                steps.add(new BaseBlock(new BlockPos(0, -i, -1), bs));
            }
            if (step != 5) {
                steps.add(new BaseBlock(new BlockPos(1, -i, -1), bs));
            }
            if (step != 4) {
                steps.add(new BaseBlock(new BlockPos(1, -i, 0), bs));
            }
            if (step != 3) {
                steps.add(new BaseBlock(new BlockPos(1, -i, 1), bs));
            }
            if (step != 2) {
                steps.add(new BaseBlock(new BlockPos(0, -i, 1), bs));
            }
            if (step != 1) {
                steps.add(new BaseBlock(new BlockPos(-1, -i, 1), bs));
            }
            if (step != 0) {
                steps.add(new BaseBlock(new BlockPos(-1, -i, 0), bs));
            }
        }
        return steps;
    }
}
