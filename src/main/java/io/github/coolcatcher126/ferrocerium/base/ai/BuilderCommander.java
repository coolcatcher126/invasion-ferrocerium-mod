package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseBlocks;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

import java.util.ArrayList;
import java.util.List;
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
            Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity(builder -> !(builder.isExchanging() || builder.isBuilding() || builder.isGathering()));
            if (bot.isPresent()) {
                if (build) {
                    List<BaseBlocks> thingsToBuild = new ArrayList<>();
                    thingsToBuild.addAll(alienBase.getBaseBlocks());
                    thingsToBuild.addAll(alienBase.getSections());
                    if (!thingsToBuild.stream().allMatch(BaseBlocks::isBuilt)) {
                        AlienBuilderBotEntity bot1 = bot.get();
                        BaseBlocks blocks = alienBase.getClosestThingToBuild(bot1.getBlockPos());
                        bot1.setToBuild(blocks);
                        bot1.setBuilding(true);
                        InvasionFerrocerium.RECIPES.requestCraftRequiredResources(bot1, blocks.getBlocks().stream().map(block -> ItemVariant.of(block.getBlockState().getBlock().asItem())).toList());
                    }
                }
                else {
                    if (!alienBase.getResources().isEmpty()) {
                        AlienBuilderBotEntity x = bot.get();
                        Vein vein = alienBase.removeClosestVein(x.getBlockPos());
                        x.setVein(vein);
                        x.setGathering(true);
                    }
                }
            }
            build = !build;
            ticksToCommand = MAX_TICKS_TO_COMMAND;
        }
    }
}
