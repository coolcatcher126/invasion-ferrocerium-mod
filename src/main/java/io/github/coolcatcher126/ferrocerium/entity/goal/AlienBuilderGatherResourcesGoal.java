package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public class AlienBuilderGatherResourcesGoal extends Goal {
    private final AlienBuilderBotEntity alienBuilderBot;

    private final int REACH_RANGE = 5;//The furthest from the AlienBuilderBotEntity a block can be broken from
    private final int MAX_BREAK_TICKS = 200;//The maximum time in ticks it takes to break a block
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
        if (alienBuilderBot.getBase() == null || alienBuilderBot.getVein() == null){
            return false;
        }
        else{
            this.path = this.alienBuilderBot.getNavigation().findPathTo(this.alienBuilderBot.getVein().getFirst(), 0);
            return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(this.alienBuilderBot.getVein().getFirst()) < 9;
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(this.alienBuilderBot.getVein().getFirst()) < 9;
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
        blockToCollect = alienBuilderBot.getVein().getFirst();
        if (blockToCollect != null){
            if (this.alienBuilderBot.getWorld().isClient() && this.alienBuilderBot.getBlockPos().getSquaredDistance(blockToCollect) < 9) {
                if (countTicksToBreak == 0) {
                    countTicksToBreak = MAX_BREAK_TICKS;
                } else {
                    countTicksToBreak--;
                }
                this.alienBuilderBot.getWorld().setBlockBreakingInfo(this.alienBuilderBot.getId(), blockToCollect, Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10));
                return;
            }


            if (this.alienBuilderBot.getBlockPos().getSquaredDistance(blockToCollect) < 9) {
                this.alienBuilderBot.getLookControl().lookAt(blockToCollect.toCenterPos());
                if (countTicksToBreak == 0) {
                    //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
                    if (this.alienBuilderBot.getWorld().breakBlock(blockToCollect, true)){
                        this.alienBuilderBot.getWorld().emitGameEvent(GameEvent.BLOCK_DESTROY, blockToCollect, GameEvent.Emitter.of(this.alienBuilderBot));
                        alienBuilderBot.getVein().remove(blockToCollect);
                    }
                }
            }
            else {
                this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
            }
        }

    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}