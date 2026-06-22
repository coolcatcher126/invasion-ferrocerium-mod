package io.github.coolcatcher126.ferrocerium.base.ai;


import io.github.coolcatcher126.ferrocerium.base.*;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import net.minecraft.util.BlockRotation;

import java.util.ArrayList;
import java.util.List;

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

        if (alienBase.getSections().isEmpty()) {
            //Create the core of the base
            addBaseSection(BaseSectionTemplates.BASE_CORE, true, new BaseSectPos(0, 0, 0), BlockRotation.NONE);
            baseSectGetAvailablePos();
        }
    }

    @Override
    public void tick() {
        //Grow the base after the timer finishes
        if (baseGrowTime > 0) {
            baseGrowTime--;
        }
        else {
            if (alienBase.getSections().stream().allMatch(BaseSection::isBuilt)) {
                growBase();
            }
            baseGrowTime = alienBase.getRandom().nextBetween(3000, 12000);
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
}
