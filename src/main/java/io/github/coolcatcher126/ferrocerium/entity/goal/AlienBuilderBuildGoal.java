package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;
import java.util.ListIterator;

/// Check if the base section to build exists. If it does, check to see if it is complete. If not, build the section.
public class AlienBuilderBuildGoal extends Goal {
    private final AlienBuilderBotEntity alienBuilderBot;
    private final double speed;
    private ListIterator<BaseBlock> blocks;
    private int delay;
    private Path path;
    BlockPos sectToBuildPos;

    public AlienBuilderBuildGoal(AlienBuilderBotEntity alienBuilderBot, double speed){
        this.alienBuilderBot = alienBuilderBot;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return false;//DEBUG - TEST GATHER RESOURCES
//            if (alienBase == null || getSection() == null){
//                return false;
//            }
//            else{
//                this.sectToBuildPos = alienBase.getOrigin().add(getSection().getOrigin().toBlockPos());
//                this.path = this.alienBuilderBot.getNavigation().findPathTo(this.sectToBuildPos, 0);
//                return !getSection().isBuilt() && (this.path != null || this.alienBuilderBot.getAttackBox().contains(sectToBuildPos.toCenterPos()));
//            }
    }

    @Override
    public boolean shouldContinue() {
        return (this.alienBuilderBot.getBase() != null && this.alienBuilderBot.getSection() != null && blocks.hasNext() && this.alienBuilderBot.isInWalkTargetRange(sectToBuildPos));
    }

    public void tick(){
        assert this.alienBuilderBot.getBase() != null;

        this.alienBuilderBot.getLookControl().lookAt(sectToBuildPos.toCenterPos());
        if (delay == 0){
            this.alienBuilderBot.getNavigation().startMovingAlong(this.path, speed);
            World world = this.alienBuilderBot.getEntityWorld();
            if (blocks.hasNext() ) {
                BaseBlock block = blocks.next();
                if (!block.isWantedBlock(world)){
                    BlockPos blockPos = block.getBlockPos().add(this.alienBuilderBot.getBase().getOrigin());
                    BlockState blockState = block.getBlockState();

                    world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.alienBuilderBot, blockState));
                }
            }
            delay = 5;
        }
        else {
            delay--;
        }
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, speed);
        this.alienBuilderBot.setBuilding(true);
        assert this.alienBuilderBot.getSection() != null;
        blocks = this.alienBuilderBot.getSection().getBaseBlockData().listIterator();
        delay = 0;
    }

    @Override
    public void stop() {
        this.alienBuilderBot.setBuilding(false);
        this.alienBuilderBot.setSection(null);
        this.alienBuilderBot.getNavigation().stop();
    }
}
