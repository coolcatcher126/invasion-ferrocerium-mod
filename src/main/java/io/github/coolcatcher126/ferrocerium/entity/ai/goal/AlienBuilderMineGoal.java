package io.github.coolcatcher126.ferrocerium.entity.ai.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;

/// Gather the resources required to build bases. Gathers wood from trees, ores and stone.
/// <p>Gathers in an area around the base</p>
public class AlienBuilderMineGoal extends AlienBuilderGatherResourcesBaseGoal {
    public AlienBuilderMineGoal(AlienBuilderBotEntity alienBuilderBot, double speed) {
        super(alienBuilderBot, speed);
    }

    @Override
    public boolean canStart() {
        return alienBuilderBot.isMining() && super.canStart() && (vein.getCategories().contains(ResourceCategory.STONE) || vein.getCategories().contains(ResourceCategory.ORES));
    }

    @Override
    public boolean shouldContinue() {
        return alienBuilderBot.isMining() && super.shouldContinue() && (vein.getCategories().contains(ResourceCategory.STONE) || vein.getCategories().contains(ResourceCategory.ORES));
    }

//    @Override
//    public void start() {
//        super.start();
//        alienBuilderBot.setMining(true);
//    }

    @Override
    public void stop() {
        alienBuilderBot.setMining(false);
        super.stop();
    }
}