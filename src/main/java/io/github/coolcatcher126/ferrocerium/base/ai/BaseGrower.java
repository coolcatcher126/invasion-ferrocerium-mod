package io.github.coolcatcher126.ferrocerium.base.ai;


import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Grows the base
 *
 * <p>Every few ticks, creates a new base section and adds it to the base section list.
 *
 * <p>Only grows the base into empty spaces
 */
public class BaseGrower implements AlienBaseTask {
    private final AlienBase alienBase;
    List<BaseSectPos> availablePos = new ArrayList<>();
    private int baseGrowTime;

    List<BaseSectionTemplate> sectionTemplateList = new ArrayList<>();

    public BaseGrower(AlienBase alienBase){
        this.alienBase = alienBase;
        this.baseGrowTime = 10;
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);

        //Create the core of the base
        addBaseSection(BaseSectionTemplates.BASE_CORE, true, new BaseSectPos(0, 0, 0), BlockRotation.NONE);
        baseSectGetAvailablePos();
    }

    @Override
    public void tick() {
        //Grow the base after the timer finishes
        if (baseGrowTime > 0) {
            baseGrowTime--;
        }
        else {
            Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity();
            if (bot.isPresent()) {
                for (BaseSection section : alienBase.getSections()) {
                    if (!section.isBuilt()) {
                        growBase();
                        break;
                    }
                }
                baseGrowTime = alienBase.getRandom().nextBetween(3000, 12000);
            }
        }
    }

    /// Grows the base at random by adding an extra base section to the base
    public void growBase(){
        int randomInt = alienBase.getRandom().nextInt(sectionTemplateList.size());
        BaseSectPos offset = availablePos.toArray(new BaseSectPos[0])[alienBase.getRandom().nextInt(availablePos.size())];
        BlockRotation rot = BlockRotation.NONE;

        addBaseSection(sectionTemplateList.get(randomInt), false, offset, rot);
    }

    private void addBaseSection(BaseSectionTemplate sectionTemplate, boolean isCore, BaseSectPos offset, BlockRotation rot){
        BaseSection newSection = new BaseSection(sectionTemplate, alienBase.getWorld(), offset, rot, isCore);
        alienBase.addBaseSection(newSection);
        availablePos.remove(offset);
        baseSectCheckAdjPos(newSection);
        newSection.setAlienBase(alienBase);

        Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity();
        bot.ifPresent(x -> {
            craftRequiredResources(x, newSection.getOrCalculateBaseBlockData().stream().map(block -> block.getBlockState().getBlock().asItem()).toList());
            x.setSection(newSection);
            x.setBuilding(true);
        });
    }

    /// Gets all the positions adjacent to a base section that itself is not occupied by a section.
    private void baseSectGetAvailablePos(){
        for (BaseSection section : alienBase.getSections()) {
            baseSectCheckAdjPos(section);
        }
    }

    private void baseSectCheckAdjPos(BaseSection section){
        BaseSectPos pos = section.getOrigin();
        BaseSectPos newPos;

        newPos = pos.add(1, 0, 0);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(-1, 0, 0);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(0, 0, 1);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(0, 0, -1);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }
    }

    private boolean checkSectionLocationClear(BaseSectPos pos){
        for (BaseSection section : alienBase.getSections()) {
            if (section.getOrigin().equals(pos)){
                return false;
            }
        }
        return true;
    }

    private void craftRequiredResources(AlienBuilderBotEntity bot, List<Item> requiredResources){
        Map<Item, Long> resMap = requiredResources.stream().collect(Collectors.groupingBy(Item::asItem, Collectors.counting()));
        for (Map.Entry<Item, Long> resEntry : resMap.entrySet()) {
            Map<Item, Integer> craftingSteps = InvasionFerrocerium.RECIPES.getCraftingStepsForItem(resEntry.getKey().asItem(), Optional.of(Math.toIntExact(resEntry.getValue())));
            if (null != craftingSteps) {
                for (Map.Entry<Item, Integer> resEntry2 : craftingSteps.entrySet()) {
                    bot.addCraftingRequest(resEntry2.getKey(), Math.toIntExact(resEntry2.getValue()));
                }
            }
        }
    }
}
