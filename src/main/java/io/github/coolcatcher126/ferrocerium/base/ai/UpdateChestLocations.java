package io.github.coolcatcher126.ferrocerium.base.ai;


import com.google.common.base.Stopwatch;
import io.github.coolcatcher126.ferrocerium.base.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
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
    private final int SEARCH_TIME = 60;
    private int search_time_count = SEARCH_TIME;

    private int sectionToSearch = 0;

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
            List<BaseSection> sections = alienBase.getSections();
            if (sectionToSearch < sections.size()) {
                BaseSection section = sections.get(sectionToSearch);
                section.updateChestLocations();
                sectionToSearch++;
            }
            else {
                sectionToSearch = 0;
            }
            search_time_count = SEARCH_TIME;
        }
    }
}
