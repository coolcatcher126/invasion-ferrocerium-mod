package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
public class AlienBuilderGatherResourcesGoal extends Goal {
    private final AlienBuilderBotEntity alienBuilderBot;
    private int delay;
    private Path path;

    public AlienBuilderGatherResourcesGoal(AlienBuilderBotEntity alienBuilderBot){
        this.alienBuilderBot = alienBuilderBot;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return false;
    }
}