package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;

import java.util.Optional;

/**
 * Command the builders to do work
 *
 * <p>Every few ticks, find an idle AlienBuilderBotEntity to complete a task
 */
public class BuilderCommander implements AlienBaseTask {
    private final AlienBase alienBase;
    private final int MAX_TICKS_TO_COMMAND = 20;
    private int ticksToCommand = MAX_TICKS_TO_COMMAND;
    boolean build = false;

    public BuilderCommander(AlienBase alienBase){
        this.alienBase = alienBase;
    }

    @Override
    public void tick() {
        if (ticksToCommand > 0) {
            ticksToCommand--;
        }
        else {
            Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity(builder -> !(builder.isExchanging() || builder.isBuilding() || builder.isGathering() || builder.isMining()));
            if (bot.isPresent()) {
                if (build) {
                    for (BaseSection section : alienBase.getSections()) {
                        if (!section.isBuilt()) {
                            AlienBuilderBotEntity bot1 = bot.get();
                            bot1.setSection(section);
                            bot1.setBuilding(true);
                            InvasionFerrocerium.RECIPES.craftRequiredResources(bot1, section.getOrCalculateBaseBlockData().stream().map(block -> block.getBlockState().getBlock().asItem()).toList());
                            break;
                        }
                    }
                }
                else {
                    if (!alienBase.getResources().isEmpty()) {
                        AlienBuilderBotEntity x = bot.get();
                        Vein vein = alienBase.removeClosestVein(x.getBlockPos());
                        x.setVein(vein);
                        if (vein.getCategories().contains(ResourceCategory.ORES) || vein.getCategories().contains(ResourceCategory.STONE)) {
                            x.setMining(true);
                        }
                        else if (vein.getCategories().contains(ResourceCategory.WOOD)) {
                            x.setGathering(true);
                        }
                    }
                }
            }
            build = !build;
            ticksToCommand = MAX_TICKS_TO_COMMAND;
        }
    }
}
