package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public class AlienBuilderGatherWoodGoal extends AlienBuilderGatherResourcesBaseGoal {

    private Vein pillar;
    protected boolean removePillar = false;

    public AlienBuilderGatherWoodGoal(AlienBuilderBotEntity alienBuilderBot, double speed) {
        super(alienBuilderBot, speed);
    }

    @Override
    public boolean canStart() {
        if (super.canStart()) {
            return true;
        }
        vein = alienBuilderBot.getVein();
        blockToCollect = (vein.isAboveVein(alienBuilderBot.getBlockPos().up()) || removePillar) ? vein.size() -1 : 0;
        this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
    }

    @Override
    public boolean shouldContinue() {
        if (super.shouldContinue()) {
            return true;
        }

        blockToCollect = (vein.isAboveVein(alienBuilderBot.getBlockPos().up()) || removePillar) ? vein.size() -1 : 0;
        this.path = this.alienBuilderBot.getNavigation().findPathTo(vein.get(blockToCollect), 0);
        return this.path != null || this.alienBuilderBot.getBlockPos().getSquaredDistance(vein.get(blockToCollect)) < SQR_REACH_RANGE;
    }

    @Override
    public void stop() {
        removePillar = false;
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        pillarUp();
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
            pillar = new Vein(true);
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