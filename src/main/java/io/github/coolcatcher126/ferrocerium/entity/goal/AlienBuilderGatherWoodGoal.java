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


    public AlienBuilderGatherWoodGoal(AlienBuilderBotEntity alienBuilderBot, double speed) {
        super(alienBuilderBot, speed);
    }
}