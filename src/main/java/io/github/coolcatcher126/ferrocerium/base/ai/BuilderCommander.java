package io.github.coolcatcher126.ferrocerium.base.ai;

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
    boolean build = true;

    public BuilderCommander(AlienBase alienBase){
        this.alienBase = alienBase;
    }

    @Override
    public void tick() {
        if (ticksToCommand > 0) {
            ticksToCommand--;
        }
        else {
            Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity();
            if (bot.isPresent()) {
                if (build) {
                    for (BaseSection section : alienBase.getSections()) {
                        if (!section.isBuilt()) {
                            AlienBuilderBotEntity bot1 = bot.get();
                            bot1.setSection(section);
                            bot1.setBuilding(true);
                            break;
                        }
                    }
                }
                else {
                    if (!alienBase.getResources().isEmpty()) {
                        Vein vein = alienBase.removeFirstVein();

                        bot.ifPresentOrElse(x -> {
                                    x.setVein(vein);
                                    x.setMining(vein.getCategories().contains(ResourceCategory.ORES) || vein.getCategories().contains(ResourceCategory.STONE));
                                    x.setGathering(vein.getCategories().contains(ResourceCategory.WOOD));
                                },
                                () -> alienBase.addVeinFirst(vein)
                        );
                    }
                }
                build = !build;
                ticksToCommand = MAX_TICKS_TO_COMMAND;
            }
        }
    }


}
