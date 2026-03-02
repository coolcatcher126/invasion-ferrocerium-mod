package io.github.coolcatcher126.ferrocerium.base.sections;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseSectionTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BaseCoreTemplate extends BaseSectionTemplate {
    public final static String name = "base_core";

    public BaseCoreTemplate() {
        super(name, true);
    }

//    @Override
//    public ArrayList<BaseBlock> getRelativeBlockData(World world) {
//        ArrayList<BaseBlock> baseBlocks = super.getRelativeBlockData(world);
//        BlockState bs = Blocks.STONE_BRICKS.getDefaultState();
//        for (int y = 0, i = 0; y >= -53; y--, i++) {
//            int step = i % 8;
//            if (step != 7) {
//                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, -1), bs));
//            }
//            if (step != 6) {
//                baseBlocks.add(new BaseBlock(new BlockPos(0, -i, -1), bs));
//            }
//            if (step != 5) {
//                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, -1), bs));
//            }
//            if (step != 4) {
//                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, 0), bs));
//            }
//            if (step != 3) {
//                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, 1), bs));
//            }
//            if (step != 2) {
//                baseBlocks.add(new BaseBlock(new BlockPos(0, -i, 1), bs));
//            }
//            if (step != 1) {
//                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, 1), bs));
//            }
//            if (step != 0) {
//                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, 0), bs));
//            }
//        }
//        return baseBlocks;
//    }
}
