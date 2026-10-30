package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseBlocks;
import io.github.coolcatcher126.ferrocerium.base.BaseSectPos;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

    List<Integer> STRIP_MINE_LEVELS = List.of(-53, 16, 48);

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
        LongArrayList mineshaft = new LongArrayList();
        ArrayList<BaseBlock> baseBlocks = new ArrayList<>();
        BlockState stoneBricksDefaultState = Blocks.STONE_BRICKS.getDefaultState();
        BlockState air = Blocks.AIR.getDefaultState();
        for (int y = alienBase.getOrigin().getY(), i = 1; y >= -53; y--, i++){
            int step = i%8;
            if (step == 7) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, -i, -1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, -i, -1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, -i, -2), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, -i, -2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 1-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 1-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 1-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 1-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 2-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 2-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 2-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 2-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 3-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 3-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 3-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 3-i, -2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, -2).asLong());
                mineshaft.add(alienBase.getOrigin().add(-1, -i, -2).asLong());
            }


            if (step == 6) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, -i, -1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, -i, -2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 1-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 1-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 2-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 2-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 3-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 3-i, -2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(0, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(0, -i, -2).asLong());
            }


            if (step == 5) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, -i, -1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, -i, -1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, -i, -2), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, -i, -2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 1-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 1-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 1-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 1-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 2-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 2-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 2-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 2-i, -2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 3-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 3-i, -1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 3-i, -2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 3-i, -2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(1, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, -2).asLong());
                mineshaft.add(alienBase.getOrigin().add(1, -i, -2).asLong());
            }

            if (step == 4) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, -i, 0), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, -i, 0), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 1-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 1-i, 0), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 2-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 2-i, 0), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 3-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 3-i, 0), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(1, -i, 0).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, 0).asLong());
            }

            if (step == 3) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, -i, 1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, -i, 1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, -i, 2), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, -i, 2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 1-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 1-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 1-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 1-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 2-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 2-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 2-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 2-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 3-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 3-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(2, 3-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(1, 3-i, 2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(1, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, 2).asLong());
                mineshaft.add(alienBase.getOrigin().add(1, -i, 2).asLong());
            }

            if (step == 2) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, -i, 1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, -i, 2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 1-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 1-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 2-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 2-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 3-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(0, 3-i, 2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(0, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(0, -i, 2).asLong());
            }

            if (step == 1) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, -i, 1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, -i, 1), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, -i, 2), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, -i, 2), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 1-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 1-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 1-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 1-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 2-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 2-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 2-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 2-i, 2), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 3-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 3-i, 1), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 3-i, 2), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 3-i, 2), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, 2).asLong());
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 2).asLong());
            }

            if (step == 0) {
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, -i, 0), stoneBricksDefaultState));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, -i, 0), stoneBricksDefaultState));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 1-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 1-i, 0), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 2-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 2-i, 0), air));

                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-1, 3-i, 0), air));
                baseBlocks.add(new BaseBlock(alienBase.getOrigin().add(-2, 3-i, 0), air));
            }
            else {
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 0).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, 0).asLong());
            }

            if (STRIP_MINE_LEVELS.contains(y) || STRIP_MINE_LEVELS.contains(y - 1) || STRIP_MINE_LEVELS.contains(y - 2)) {
                mineshaft.add(alienBase.getOrigin().add(-3, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(-1, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(0, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(1, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, -3).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, -3).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, -2).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, -2).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, -1).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, -1).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, 0).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, 0).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, 1).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, 1).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, 2).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, 2).asLong());

                mineshaft.add(alienBase.getOrigin().add(-3, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(-2, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(-1, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(0, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(1, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(2, -i, 3).asLong());
                mineshaft.add(alienBase.getOrigin().add(3, -i, 3).asLong());
            }
            if (i % 10 == 0 || i == 1){
                //Split the mineshaft staircase into sections of 10 blocks down
                alienBase.addBaseBlocks(new BaseBlocks(alienBase.getWorld(), new BaseSectPos(baseBlocks.getLast().getBlockPos().subtract(alienBase.getOrigin())), baseBlocks));
                baseBlocks = new ArrayList<>();
                alienBase.addVein(new Vein(mineshaft, EnumSet.of(ResourceCategory.STONE, ResourceCategory.ORES), true));
                mineshaft = new LongArrayList();
            }
        }
        //Split the mineshaft staircase into sections of 10 blocks down
        alienBase.addBaseBlocks(new BaseBlocks(alienBase.getWorld(), new BaseSectPos(baseBlocks.getLast().getBlockPos().subtract(alienBase.getOrigin())), baseBlocks));
        alienBase.addVein(new Vein(mineshaft, EnumSet.of(ResourceCategory.STONE, ResourceCategory.ORES), true));
    }

    void extendStripMines(int length) {
        for (Integer y : STRIP_MINE_LEVELS) {
            if (y < alienBase.getOrigin().getY()) {
                BlockPos mineshaftCenter = new BlockPos(alienBase.getOrigin().getX(), y, alienBase.getOrigin().getZ());
                extendStripMine(mineshaftCenter, Direction.NORTH, length);
                extendStripMine(mineshaftCenter, Direction.EAST, length);
                extendStripMine(mineshaftCenter, Direction.SOUTH, length);
                extendStripMine(mineshaftCenter, Direction.WEST, length);
            }
        }

    }

    private void extendStripMine(BlockPos mineFront, Direction direction, int length) {
        LongArrayList mineshaft = new LongArrayList();

        //Start the side branches outside the stairwell
        mineFront = mineFront.offset(direction, 4);
        while (alienBase.getWorld().getBlockState(mineFront).isAir()) {
            mineFront = mineFront.offset(direction);
        }

        //Add the blocks to mine
        for (int i = 0; i < length; i++) {
            mineshaft.add(mineFront.asLong());
            mineshaft.add(mineFront.add(0, 1, 0).asLong());
            mineshaft.add(mineFront.add(0, 2, 0).asLong());
            mineFront = mineFront.offset(direction);
        }
        alienBase.addVein(new Vein(mineshaft));
    }
}
