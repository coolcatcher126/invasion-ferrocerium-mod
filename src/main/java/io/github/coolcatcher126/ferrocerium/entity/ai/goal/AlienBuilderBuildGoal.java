package io.github.coolcatcher126.ferrocerium.entity.ai.goal;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
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

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;

/// Check if the base section to build exists. If it does, check to see if it is complete. If not, build the section.
public class AlienBuilderBuildGoal extends Goal {
    protected final int SQR_REACH_RANGE = 25;//The furthest from the AlienBuilderBotEntity a block can be placed

    private final AlienBuilderBotEntity alienBuilderBot;
    private final CraftGoal craftGoal;
    private final double speed;
    private LinkedList<BaseBlock> blocks;
    private int delay;
    private Path path;
    BlockPos sectToBuildPos;
    Set<Item> blockPallete;
    BaseSection prevSection;

    public AlienBuilderBuildGoal(AlienBuilderBotEntity alienBuilderBot, CraftGoal craftGoal, double speed){
        this.alienBuilderBot = alienBuilderBot;
        this.craftGoal = craftGoal;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.alienBuilderBot.isBuilding() || this.alienBuilderBot.getBase() == null || this.alienBuilderBot.getSection() == null){
            return false;
        }

        if (prevSection != this.alienBuilderBot.getSection()) {
            prevSection = this.alienBuilderBot.getSection();
            blockPallete = this.alienBuilderBot.getSection().getBaseBlockPallete();
        }

        this.sectToBuildPos = this.alienBuilderBot.getBase().getOrigin().add(this.alienBuilderBot.getSection().getOrigin().toBlockPos());
        this.path = this.alienBuilderBot.getNavigation().findPathTo(this.sectToBuildPos, 0);
        return !this.alienBuilderBot.getSection().isBuilt() && (this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(sectToBuildPos.toCenterPos()) < SQR_REACH_RANGE);
    }

    @Override
    public boolean shouldContinue() {
        if (!this.alienBuilderBot.isBuilding() || this.alienBuilderBot.getBase() == null || this.alienBuilderBot.getSection() == null){
            return false;
        }

        if (prevSection != this.alienBuilderBot.getSection()) {
            prevSection = this.alienBuilderBot.getSection();
            blockPallete = this.alienBuilderBot.getSection().getBaseBlockPallete();
        }

        return (this.alienBuilderBot.getInventory().containsAny(blockPallete) && blocks.peekFirst() != null && this.alienBuilderBot.isInWalkTargetRange(sectToBuildPos));
    }


    public void tick(){
        assert this.alienBuilderBot.getBase() != null;

        BaseBlock block = blocks.peekFirst();
        if (block != null) {
            BlockPos blockPos = this.alienBuilderBot.getBase().getOrigin().add(block.getBlockPos());

            World world = this.alienBuilderBot.getEntityWorld();

            if (block.isWantedBlock(world)) {
                return;
            }

            BlockState blockState = block.getBlockState();
            Item blockItem = blockState.getBlock().asItem();
            if (!alienBuilderBot.getInventory().containsAny(x -> x.getItem() == blockItem)) {
                craftGoal.addCraftingRequest(blockItem);
                return;
            }


            this.alienBuilderBot.getLookControl().lookAt(blockPos.toCenterPos());

            if (!(this.alienBuilderBot.getBlockPos().getSquaredDistance(blockPos.toCenterPos()) < SQR_REACH_RANGE)) {
                this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
                return;
            }

            if (delay > 0) {
                delay--;
                return;
            }
            delay = 5;

            alienBuilderBot.swingHand(Hand.MAIN_HAND);

            world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
            world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.alienBuilderBot, blockState));
            blocks.remove(block);
            alienBuilderBot.getInventory().removeItem(blockItem, 1);
        }
    }

    @Override
    public void start() {
        this.alienBuilderBot.getNavigation().startMovingAlong(this.path, this.speed);
        //this.alienBuilderBot.setBuilding(true);
        assert this.alienBuilderBot.getSection() != null;
        blocks = new LinkedList<>(this.alienBuilderBot.getSection().getBaseBlockData());
        delay = 0;
    }

    @Override
    public void stop() {
        this.alienBuilderBot.setBuilding(false);
        //this.alienBuilderBot.setSection(null);
        this.alienBuilderBot.getNavigation().stop();
    }
}
