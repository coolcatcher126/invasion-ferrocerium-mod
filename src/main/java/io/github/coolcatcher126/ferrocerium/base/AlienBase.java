package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;
import java.util.random.RandomGenerator;

/// A single base.
/// Each base owns one or more Alien Builder Bots.
public class AlienBase {
    BlockPos origin;
    ArrayList<BaseSection> sections;
    ArrayList<AlienBuilderBotEntity> builders;

    Random random;

    public AlienBase(BlockPos origin, BaseSection core, AlienBuilderBotEntity initialBuilder)
    {
        this.random = new Random();

        this.origin = origin;

        this.sections = new ArrayList<>();
        this.builders = new ArrayList<>();

        this.sections.add(core);
        this.builders.add(initialBuilder);
    }

    /// Grows the base at random by adding an extra base section to the base
    public void GrowBase(){
        int baseSectionType = random.nextInt();
        //TODO: Randomly generate a random base section to build and assign an unemployed builder bot to build it.
    }

    /// Adds a builder to the builders
    public void HireBuilder(AlienBuilderBotEntity builder){
        builders.add(builder);
    }
}
