package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Designates the location of a mineshaft
 *
 * <p>The mineshaft is the series of blocks to be mined and placed.
 *
 * <p>Blocks to be mined will be placed into a {@link io.github.coolcatcher126.ferrocerium.resources.Vein Vein} object using {@link AlienBase#addVein(Vein)}}
 *
 * <p>Grows the mineshaft every few ticks
 */
public class CreateMineshaft implements AlienBaseTask {
    private final AlienBase alienBase;
    private final int SEARCH_TIME = 1200;
    private int search_time_count = SEARCH_TIME;

    List<Integer> STRIP_MINE_LEVELS = List.of(-53, -52, 16, 17, 48, 49);

    public CreateMineshaft(AlienBase alienBase){
        this.alienBase = alienBase;
        createMineshaft();
    }

    @Override
    public void tick() {
        //Don't look for things to mine all the time
        if (search_time_count > 0){
            search_time_count--;
        }
        else{
            extendStripMines(5);
            search_time_count = SEARCH_TIME;
        }
    }

    /// Creates a mineshaft starting at the middle of the base going down using a spiral staircase.
    /// <p>Uses a strip mining from y-levels 48 (for aluminium and copper), 16 (for iron) and -53 (for diamonds) </p>
    private void createMineshaft(){
        ArrayList<BlockPos> mineshaft = new ArrayList<>();
        for (int y = alienBase.getOrigin().getY(), i = 0; y >= -53; y--, i++){
            int step = i%8;
            if (step != 7) {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, -1));
            }
            if (step != 6) {
                mineshaft.add(alienBase.getOrigin().add(0, -i, -1));
            }
            if (step != 5) {
                mineshaft.add(alienBase.getOrigin().add(1, -i, -1));
            }
            if (step != 4) {
                mineshaft.add(alienBase.getOrigin().add(1, -i, 0));
            }
            if (step != 3) {
                mineshaft.add(alienBase.getOrigin().add(1, -i, 1));
            }
            if (step != 2) {
                mineshaft.add(alienBase.getOrigin().add(0, -i, 1));
            }
            if (step != 1) {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 1));
            }
            if (step != 0) {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 0));
            }

            if (STRIP_MINE_LEVELS.contains(y)) {
                mineshaft.add(alienBase.getOrigin().add(-2,-i,-2));
                mineshaft.add(alienBase.getOrigin().add(-1,-i,-2));
                mineshaft.add(alienBase.getOrigin().add(0,-i,-2));
                mineshaft.add(alienBase.getOrigin().add(1,-i,-2));
                mineshaft.add(alienBase.getOrigin().add(2,-i,-2));

                mineshaft.add(alienBase.getOrigin().add(-2,-i,-1));
                mineshaft.add(alienBase.getOrigin().add(2,-i,-1));

                mineshaft.add(alienBase.getOrigin().add(-2,-i,0));
                mineshaft.add(alienBase.getOrigin().add(2,-i,0));

                mineshaft.add(alienBase.getOrigin().add(-2,-i,1));
                mineshaft.add(alienBase.getOrigin().add(2,-i,1));

                mineshaft.add(alienBase.getOrigin().add(-2,-i,2));
                mineshaft.add(alienBase.getOrigin().add(-1,-i,2));
                mineshaft.add(alienBase.getOrigin().add(0,-i,2));
                mineshaft.add(alienBase.getOrigin().add(1,-i,2));
                mineshaft.add(alienBase.getOrigin().add(2,-i,2));

                if (i%2 == 0){
                    //Split the mineshaft staircase into sections
                    alienBase.addVein(new Vein(mineshaft, EnumSet.of(ResourceCategory.STONE, ResourceCategory.ORES), true));
                    mineshaft = new ArrayList<>();
                }
            }
        }

        createStairwell();
    }

    void createStairwell() {
        BlockState bs = Blocks.STONE_BRICKS.getDefaultState();
        for (int y = 0, i = 0; y >= -53; y--, i++) {
            int step = i % 8;
            if (step != 7) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(-1, -i, -1), bs));
            }
            if (step != 6) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(0, -i, -1), bs));
            }
            if (step != 5) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(1, -i, -1), bs));
            }
            if (step != 4) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(1, -i, 0), bs));
            }
            if (step != 3) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(1, -i, 1), bs));
            }
            if (step != 2) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(0, -i, 1), bs));
            }
            if (step != 1) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(-1, -i, 1), bs));
            }
            if (step != 0) {
                alienBase.addBaseBlock(new BaseBlock(new BlockPos(-1, -i, 0), bs));
            }
        }
    }

    void extendStripMines(int length) {
        for (Integer y : STRIP_MINE_LEVELS) {
            if (y < alienBase.getOrigin().getY() && y % 2 == 0) {
                BlockPos mineshaftCenter = new BlockPos(alienBase.getOrigin().getX(), y, alienBase.getOrigin().getZ());
                extendStripMine(mineshaftCenter, Direction.NORTH, length);
                extendStripMine(mineshaftCenter, Direction.EAST, length);
                extendStripMine(mineshaftCenter, Direction.SOUTH, length);
                extendStripMine(mineshaftCenter, Direction.WEST, length);
            }
        }

    }

    private void extendStripMine(BlockPos mineFront, Direction direction, int length) {
        ArrayList<BlockPos> mineshaft = new ArrayList<>();

        //Start the side branches outside the stairwell
        mineFront = mineFront.offset(direction, 2);
        while (alienBase.getWorld().getBlockState(mineFront).isAir()) {
            mineFront = mineFront.offset(direction);
        }

        //Add the blocks to mine
        for (int i = 0; i < length; i++) {
            mineshaft.add(mineFront);
            mineshaft.add(mineFront.add(0, 1, 0));
            mineFront = mineFront.offset(direction);
        }
        alienBase.addVein(new Vein(mineshaft, EnumSet.of(ResourceCategory.STONE, ResourceCategory.ORES), true));
    }
}
