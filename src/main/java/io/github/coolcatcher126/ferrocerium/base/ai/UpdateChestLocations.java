package io.github.coolcatcher126.ferrocerium.base.ai;


import io.github.coolcatcher126.ferrocerium.base.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Grows the base
 *
 * <p>Every few ticks, creates a new base section and adds it to the base section list.
 *
 * <p>Only grows the base into empty spaces
 */
public class UpdateChestLocations implements AlienBaseTask {
    private final AlienBase alienBase;
    private final int SEARCH_TIME = 1200;
    private int search_time_count = SEARCH_TIME;

    public UpdateChestLocations(AlienBase alienBase){
        this.alienBase = alienBase;
    }

    @Override
    public void tick() {
        //Grow the base after the timer finishes
        if (search_time_count > 0) {
            search_time_count--;
        }
        else {
            alienBase.resetChestLocations();
            for (BaseSection section : alienBase.getSections()) {
                section.getChestLocations().forEach(alienBase::addChestLocation);
            }
            search_time_count = SEARCH_TIME;
        }
    }
}
