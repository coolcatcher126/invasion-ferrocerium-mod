package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public class AlienBuilderGatherResourcesGoal extends Goal {
    private final AlienBuilderBotEntity alienBuilderBot;

    private final int SQR_REACH_RANGE = 25;//The furthest from the AlienBuilderBotEntity a block can be broken from
    private final int MAX_BREAK_TICKS = 60;//The maximum time in ticks it takes to break a block
    private final double speed;

    private int countTicksToBreak = 0;

    private Path path;

    private Vein pillar;
    private Vein vein;
    private int blockToCollect;
    private boolean removePillar = false;

    public AlienBuilderGatherResourcesGoal(AlienBuilderBotEntity alienBuilderBot, double speed){
        this.alienBuilderBot = alienBuilderBot;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (alienBuilderBot.getBase() == null || alienBuilderBot.getVein() == null || alienBuilderBot.getVein().size() == 0) {
            return false;
        }
        else {
            vein = alienBuilderBot.getVein();
            blockToCollect = 0;
            this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
            return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (vein.size() == 0) {
            return false;
        }
        blockToCollect = 0;
        this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
        alienBuilderBot.setGathering(true);
    }

    @Override
    public void stop() {
        alienBuilderBot.setGathering(false);
        vein = null;
        removePillar = false;
        this.alienBuilderBot.getNavigation().stop();
    }

    public void tick(){
        if (vein.size() == 0) {
            return;
        }
        blockToCollect = removePillar ? vein.size() -1 : 0;
        if (vein.get(blockToCollect) == null) {
            return;
        }

        if (this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE) {
           this.alienBuilderBot.getLookControl().lookAt(vein.get(blockToCollect).toCenterPos());
           breakingInfoTick();
           mineBlocks();
        }
       else {
            boolean canMove = this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
            if (canMove) {
                return;
            }
            pillarUp();
       }
    }

    private void breakingInfoTick(){
        if (countTicksToBreak >= 0) {
            countTicksToBreak--;
            int progress = Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10);
            this.alienBuilderBot.getWorld().setBlockBreakingInfo(this.alienBuilderBot.getId(), vein.get(blockToCollect), progress);

            return;
        }
        countTicksToBreak = MAX_BREAK_TICKS;
    }

    private void mineBlocks(){
        if (countTicksToBreak > 0) {
            countTicksToBreak--;
        } else {
            countTicksToBreak = MAX_BREAK_TICKS;
            if (!this.alienBuilderBot.getBase().blockIsCollectible(vein.get(blockToCollect))){
                alienBuilderBot.getVein().remove(blockToCollect);
                return;
            }
            //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
            Block blockMined = this.alienBuilderBot.getWorld().getBlockState(vein.get(blockToCollect)).getBlock();
            ItemStack blockMinedAsItem = new ItemStack(blockMined.asItem(), 1);
            if (this.alienBuilderBot.getWorld().breakBlock(vein.get(blockToCollect), false) && this.alienBuilderBot.getInventory().canInsert(blockMinedAsItem)){
                this.alienBuilderBot.getWorld().emitGameEvent(GameEvent.BLOCK_DESTROY, vein.get(blockToCollect), GameEvent.Emitter.of(this.alienBuilderBot));
                this.alienBuilderBot.getInventory().addStack(blockMinedAsItem);
                alienBuilderBot.getVein().remove(blockToCollect);
            }
        }
    }

    private void pillarUp(){
        BlockPos botPos = this.alienBuilderBot.getBlockPos();

        if (vein.get(blockToCollect).getY() - botPos.getY() <= 3) {
            if (pillar != null) {
                vein.append(pillar);
                pillar = null;
                removePillar = true;
            }
            return;
        }

        if (countTicksToBreak >= 0) {
            countTicksToBreak--;
            return;
        }
        countTicksToBreak = MAX_BREAK_TICKS;
        //If the block is too high: pillar up.
        ItemStack stack = null;
        Block block = null;
        for (int i = 0; i < this.alienBuilderBot.getInventory().size(); i++) {
            stack = this.alienBuilderBot.getInventory().getStack(i);
            block = Block.getBlockFromItem(stack.getItem());
            if (block != Blocks.AIR) {
                this.alienBuilderBot.setStackInHand(Hand.MAIN_HAND, stack);
                break;
            }
        }
        if (block == null) {
            return;
        }
        if (pillar == null) {
            pillar = new Vein();
        }
        this.alienBuilderBot.setJumping(true);
        this.alienBuilderBot.jump();
        BlockState stateFromComponent = block.getDefaultState();
        this.alienBuilderBot.getWorld().setBlockState(botPos, stateFromComponent);
        stack.decrement(1);
        pillar.add(botPos);
        vein.remove(botPos);
        this.alienBuilderBot.setVein(vein);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}