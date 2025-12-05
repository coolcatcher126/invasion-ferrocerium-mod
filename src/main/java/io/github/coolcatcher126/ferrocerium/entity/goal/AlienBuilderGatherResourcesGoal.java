package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public class AlienBuilderGatherResourcesGoal extends Goal {
    private final AlienBuilderBotEntity alienBuilderBot;

    private final int REACH_RANGE = 5;//The furthest from the AlienBuilderBotEntity a block can be broken from
    private final int MAX_BREAK_TICKS = 200;//The maximum time in ticks it takes to break a block

    private int minBlockSearchRadius = 10;//The minimum distance to search for blocks to collect (centered on the base)
    private int maxBlockSearchRadius = 20;//The maximum distance to search for blocks to collect (centered on the base)

    private int delay;
    private Path path;

    private BlockPos blockToCollect;
    private AlienBase alienBase;//The base in which to collect resources around and to deposit the materials.

    public AlienBuilderGatherResourcesGoal(AlienBuilderBotEntity alienBuilderBot){
        this.alienBuilderBot = alienBuilderBot;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return !this.alienBuilderBot.isBuilding();
    }

    @Override
    public boolean shouldContinue() {
        return !this.alienBuilderBot.isBuilding();
    }

    @Override
    public void start() {
        alienBuilderBot.setGathering(true);
    }

    @Override
    public void stop() {
        alienBuilderBot.setGathering(false);
    }

    public void tick(){
        //TODO: Find block within the search area


        //TODO: make the bot move towards the block


        //TODO: make the bot  mine it as well as all/many other adjacent blocks of the same type.



        if (blockToCollect != null){
            this.alienBuilderBot.getLookControl().lookAt(blockToCollect.toCenterPos());
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    public void setBlockSearchRadius(int minBlockSearchRadius, int maxBlockSearchRadius) {
        this.minBlockSearchRadius = minBlockSearchRadius;
        this.maxBlockSearchRadius = maxBlockSearchRadius;
    }
}