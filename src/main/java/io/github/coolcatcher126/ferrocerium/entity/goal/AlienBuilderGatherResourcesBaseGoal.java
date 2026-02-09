package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public abstract class AlienBuilderGatherResourcesBaseGoal extends Goal {
    protected final AlienBuilderBotEntity alienBuilderBot;

    protected final int SQR_REACH_RANGE = 25;//The furthest from the AlienBuilderBotEntity a block can be broken from
    protected final int MAX_BREAK_TICKS = 60;//The maximum time in ticks it takes to break a block
    protected final double speed;

    protected int countTicksToBreak = 0;

    protected Path path;


    protected Vein vein;
    protected int blockToCollect;

    public AlienBuilderGatherResourcesBaseGoal(AlienBuilderBotEntity alienBuilderBot, double speed){
        this.alienBuilderBot = alienBuilderBot;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        vein = alienBuilderBot.getVein();
        if (alienBuilderBot.getBase() == null || vein == null || vein.size() == 0) {
            return false;
        }
        blockToCollect = getBlockToCollect();
        this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
    }

    @Override
    public boolean shouldContinue() {
        if (vein.size() == 0) {
            return false;
        }
        blockToCollect = getBlockToCollect();
        this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
    }

    @Override
    public void stop() {
        //vein = null;
        this.alienBuilderBot.getNavigation().stop();
    }


    public void tick() {
        gatherTick();
    }

    /// base logic for resource gathering ran on each tick.
    protected boolean gatherTick()
    {
        if (vein.size() == 0) {
            return false;
        }
        blockToCollect = getBlockToCollect();
        if (vein.get(blockToCollect) == null) {
            vein.remove(blockToCollect);
            return false;
        }

        if (this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE) {
           this.alienBuilderBot.getLookControl().lookAt(vein.get(blockToCollect).toCenterPos());
           breakingInfoTick();
           mineBlocks();
           return  false;
        }

        boolean canMove = this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
        return !canMove;
    }

    protected int getBlockToCollect(){
        return (vein.isAboveVein(alienBuilderBot.getBlockPos().up())) ? (vein.size() - 1) : 0;
    }

    /// Sets the block breaking info on the block being mined
    private void breakingInfoTick(){
        if (countTicksToBreak >= 0) {
            countTicksToBreak--;
            int progress = Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10);
            this.alienBuilderBot.getWorld().setBlockBreakingInfo(this.alienBuilderBot.getId(), vein.get(blockToCollect), progress);
            alienBuilderBot.swingHand(Hand.MAIN_HAND);
            return;
        }
        countTicksToBreak = MAX_BREAK_TICKS;
    }

    /// Allows the entity to mine blocks. Shared between all mining and gathering goals
    private void mineBlocks(){
        if (alienBuilderBot.getWorld().isAir(vein.get(blockToCollect)) || !(vein.isShouldMineAnyways() || this.alienBuilderBot.getBase().blockIsCollectible(vein.get(blockToCollect)))){
            vein.remove(blockToCollect);
            return;
        }

        if (countTicksToBreak > 0) {
            countTicksToBreak--;
        } else {
            countTicksToBreak = MAX_BREAK_TICKS;

            //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
            Block blockMined = this.alienBuilderBot.getWorld().getBlockState(vein.get(blockToCollect)).getBlock();
            ItemStack blockMinedAsItem = new ItemStack(blockMined.asItem(), 1);
            if (this.alienBuilderBot.getWorld().breakBlock(vein.get(blockToCollect), false) && this.alienBuilderBot.getInventory().canInsert(blockMinedAsItem)){
                this.alienBuilderBot.getWorld().emitGameEvent(GameEvent.BLOCK_DESTROY, vein.get(blockToCollect), GameEvent.Emitter.of(this.alienBuilderBot));
                blockMinedAsItem = this.alienBuilderBot.addItem(blockMinedAsItem);
                if (!blockMinedAsItem.isEmpty()) {
                    LookTargetUtil.give(alienBuilderBot, blockMinedAsItem, alienBuilderBot.getPos().add(0.0, 1.0, 0.0));
                }

                vein.remove(blockToCollect);
            }
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}