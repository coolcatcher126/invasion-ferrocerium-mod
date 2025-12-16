package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
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

    private BlockPos blockToCollect;

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
        else{
            blockToCollect = alienBuilderBot.getVein().getBottom();
            this.path = this.alienBuilderBot.getNavigation().findPathTo(blockToCollect, 0);
            return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(blockToCollect) < SQR_REACH_RANGE;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (alienBuilderBot.getVein().size() == 0) {
            return false;
        }
        blockToCollect = alienBuilderBot.getVein().getBottom();
        this.path = this.alienBuilderBot.getNavigation().findPathTo(blockToCollect, 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(blockToCollect) < SQR_REACH_RANGE;
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
        alienBuilderBot.setGathering(true);
    }

    @Override
    public void stop() {
        alienBuilderBot.setGathering(false);
        this.alienBuilderBot.setVein(null);
        this.alienBuilderBot.getNavigation().stop();
    }

    public void tick(){
        if (alienBuilderBot.getVein().size() == 0) {
            return;
        }
        blockToCollect = alienBuilderBot.getVein().getBottom();
        if (blockToCollect != null){
             if (this.alienBuilderBot.getBlockPos().getSquaredDistance(blockToCollect) < SQR_REACH_RANGE) {
                this.alienBuilderBot.getLookControl().lookAt(blockToCollect.toCenterPos());
                if (this.alienBuilderBot.getWorld().isClient()){
                    if (countTicksToBreak >= 0) {
                        countTicksToBreak--;
                        this.alienBuilderBot.getWorld().setBlockBreakingInfo(this.alienBuilderBot.getId(), blockToCollect, Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10));
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
                    if (this.alienBuilderBot.getWorld().breakBlock(blockToCollect, true)){
                        this.alienBuilderBot.getWorld().emitGameEvent(GameEvent.BLOCK_DESTROY, blockToCollect, GameEvent.Emitter.of(this.alienBuilderBot));
                        alienBuilderBot.getVein().remove(blockToCollect);
                    }
                }
            }
            else {
                 boolean canMove = this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
                 if (!canMove) {
                     BlockPos botPos = this.alienBuilderBot.getBlockPos();
                     if (blockToCollect.getY() - botPos.getY() > 3) {
                         //If the block is too high: pillar up.
                         ItemStack stack = this.alienBuilderBot.getMainHandStack();
                         Block block = Block.getBlockFromItem(stack.getItem());
                         if (block != Blocks.AIR) {
                             this.alienBuilderBot.setJumping(true);
                             this.alienBuilderBot.jump();
                             BlockState stateFromComponent = block.getDefaultState();
                             this.alienBuilderBot.getWorld().setBlockState(botPos/*.add(0,1,0)*/, stateFromComponent);
                             stack.decrement(1);
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