package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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

    public AlienBuilderGatherResourcesGoal(AlienBuilderBotEntity alienBuilderBot, double speed){
        this.alienBuilderBot = alienBuilderBot;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (alienBuilderBot.getBase() == null || alienBuilderBot.getVein() == null || alienBuilderBot.getVein().size() == 0) {
            return false;
        }
        else {
            if (pillar != null) {
                vein = pillar;
                pillar = null;
            }
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
        this.alienBuilderBot.getNavigation().stop();
    }

    public void tick(){
        if (vein.size() == 0) {
            return;
        }
        blockToCollect = 0;
        if (vein.get(blockToCollect) != null){
             if (this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE) {
                this.alienBuilderBot.getLookControl().lookAt(vein.get(blockToCollect).toCenterPos());
                if (this.alienBuilderBot.getWorld().isClient()){
                    if (countTicksToBreak >= 0) {
                        countTicksToBreak--;
                        this.alienBuilderBot.getWorld().setBlockBreakingInfo(this.alienBuilderBot.getId(), vein.get(blockToCollect), Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10));
                        return;
                    }
                    countTicksToBreak = MAX_BREAK_TICKS;
                    return;
                }


                if (countTicksToBreak >= 0) {
                    countTicksToBreak--;
                } else {
                    countTicksToBreak = MAX_BREAK_TICKS;
                    //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
                    if (this.alienBuilderBot.getWorld().breakBlock(vein.get(blockToCollect), true)){
                        this.alienBuilderBot.getWorld().emitGameEvent(GameEvent.BLOCK_DESTROY, vein.get(blockToCollect), GameEvent.Emitter.of(this.alienBuilderBot));
                        alienBuilderBot.getVein().remove(blockToCollect);
                    }
                }
            }
            else {
                 boolean canMove = this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
                 if (!canMove) {
                     BlockPos botPos = this.alienBuilderBot.getBlockPos();
                     if (vein.get(blockToCollect).getY() - botPos.getY() > 3) {
                         //If the block is too high: pillar up.
                         ItemStack stack = this.alienBuilderBot.getMainHandStack();
                         Block block = Block.getBlockFromItem(stack.getItem());
                         if (block != Blocks.AIR) {
                             if (pillar == null){
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
                     }
                 }
            }
        }

    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}