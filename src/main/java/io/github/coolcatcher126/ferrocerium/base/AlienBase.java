package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Optional;

/// A single base.
/// Each base owns one or more Alien Builder Bots.
public class AlienBase {
    BlockPos origin;
    ArrayList<BaseSection> sections;
    ArrayList<AlienBuilderBotEntity> builders;

    ArrayList<BaseSectionTemplate> sectionTemplateList;
    World world;

    Random random;

    public AlienBase(World world, BlockPos origin, BaseSection core, AlienBuilderBotEntity initialBuilder)
    {
        this.world = world;
        this.origin = origin;

        this.sections = new ArrayList<>();
        this.builders = new ArrayList<>();

        this.sections.add(core);
        this.builders.add(initialBuilder);


        sectionTemplateList = new ArrayList<>();
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);
        this.random = this.world.getRandom();

        //Create the core of the base
        addBaseSection(BaseSectionTemplates.BASE_CORE, true, new BlockPos(0, 0, 0), BlockRotation.NONE);
    }

    /// Grows the base at random by adding an extra base section to the base
    public void growBase(){
        //TODO: Randomly generate a position and rotation to attach the new section.
        int randomInt = random.nextInt(sectionTemplateList.size());
        BlockPos offset = new BlockPos(0, 0, 0);
        BlockRotation rot = BlockRotation.NONE;

        addBaseSection(sectionTemplateList.get(randomInt), false, offset, rot);
    }

    private void addBaseSection(BaseSectionTemplate sectionTemplate, boolean isCore, BlockPos offset, BlockRotation rot){
        BaseSection newSection = new BaseSection(sectionTemplate, world, origin.add(offset), rot, false);
        sections.add(newSection);

        Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
        bot.ifPresent(x -> x.setSection(newSection));
    }

    /// Returns the first alien builder bot to not be building.
    private Optional<AlienBuilderBotEntity> getFirstAvailableAlienBuilderBotEntity(){
        for (AlienBuilderBotEntity builder : builders) {
            if (!builder.isBuilding()){
                return Optional.of(builder);
            }
        }
        return Optional.empty();
    }

    /// Adds a builder to the builders
    public void HireBuilder(AlienBuilderBotEntity builder){
        builders.add(builder);
    }

    /// Ticks this alien base.
    public void tick(){
        if (random.nextInt(2) == 0) {
            //TODO: Make the base grow, spawning builders as needed.
        }
    }
}
