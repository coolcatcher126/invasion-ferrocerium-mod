package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/// Check if the base section to build exists. If it does, check to see if it is complete. If not, build the section.
public class AlienBuilderBuildGoal extends Goal {
    protected final int SQR_REACH_RANGE = 25;//The furthest from the AlienBuilderBotEntity a block can be placed

    private final AlienBuilderBotEntity alienBuilderBot;
    private final double speed;
    private LinkedList<BaseBlock> blocks;
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
            if (this.alienBuilderBot.getBase() == null || this.alienBuilderBot.getSection() == null || this.alienBuilderBot.getInventory().containsAny(this.alienBuilderBot.getSection().getBaseBlockPallete())){
                return false;
            }
            else{
                this.sectToBuildPos = this.alienBuilderBot.getBase().getOrigin().add(this.alienBuilderBot.getSection().getOrigin().toBlockPos());
                this.path = this.alienBuilderBot.getNavigation().findPathTo(this.sectToBuildPos, 0);
                return !this.alienBuilderBot.getSection().isBuilt() && (this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(sectToBuildPos.toCenterPos()) < SQR_REACH_RANGE);
            }
    }

    @Override
    public boolean shouldContinue() {
        return (this.alienBuilderBot.getBase() != null && this.alienBuilderBot.getSection() != null && this.alienBuilderBot.getInventory().containsAny(this.alienBuilderBot.getSection().getBaseBlockPallete()) && blocks.peekFirst() != null && this.alienBuilderBot.isInWalkTargetRange(sectToBuildPos));
    }

    public void tick(){
        assert this.alienBuilderBot.getBase() != null;

        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, speed);

        BaseBlock block = blocks.peekFirst();
        if (block != null) {
            BlockPos blockPos = this.alienBuilderBot.getBase().getOrigin().add(block.getBlockPos());
            this.alienBuilderBot.getLookControl().lookAt(blockPos.toCenterPos());

            if (delay == 0){
                World world = this.alienBuilderBot.getEntityWorld();

                if (!block.isWantedBlock(world)){
                    BlockState blockState = block.getBlockState();
                    if (alienBuilderBot.getInventory().containsAny(alienBuilderBot.getSection().getBaseBlockPallete())) {
                        alienBuilderBot.swingHand(Hand.MAIN_HAND);

                        world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
                        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.alienBuilderBot, blockState));
                        blocks.remove(block);
                        Item blockItem = blockState.getBlock().asItem();
                        alienBuilderBot.getInventory().removeItem(blockItem,1);
                        delay = 5;
                    }
                }
            }
            else {
                delay--;
            }
        }
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, speed);
        this.alienBuilderBot.setBuilding(true);
        assert this.alienBuilderBot.getSection() != null;
        blocks = new LinkedList<>(this.alienBuilderBot.getSection().getBaseBlockData());
        delay = 0;
    }

    @Override
    public void stop() {
        this.alienBuilderBot.setBuilding(false);
        this.alienBuilderBot.setSection(null);
        this.alienBuilderBot.getNavigation().stop();
    }
}
