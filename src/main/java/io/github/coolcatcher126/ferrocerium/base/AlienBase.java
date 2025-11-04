package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

/// A single base.
/// Each base owns one or more Alien Builder Bots.
public class AlienBase {
    BlockPos origin;
    ArrayList<BaseSection> sections;
    ArrayList<AlienBuilderBotEntity> builders;
    ArrayList<BaseSectPos> availablePos;

    ArrayList<BaseSectionTemplate> sectionTemplateList;
    World world;
    protected final Random random = Random.create();
    UUID uuid = MathHelper.randomUuid(this.random);

    public AlienBase(World world, BlockPos origin, ArrayList<BaseSection> sections, ArrayList<AlienBuilderBotEntity> builders, UUID uuid){
        this.world = world;
        this.origin = origin;

        this.sections = sections;
        this.builders = builders;
        this.uuid = uuid;


        sectionTemplateList = new ArrayList<>();
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);
        this.availablePos = new ArrayList<>();
        baseSectGetAvailablePos();
    }

    public AlienBase(World world, BlockPos origin, AlienBuilderBotEntity initialBuilder)
    {
        this.world = world;
        this.origin = origin;

        this.sections = new ArrayList<>();
        this.builders = new ArrayList<>();

        this.builders.add(initialBuilder);


        sectionTemplateList = new ArrayList<>();
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);

        //Create the core of the base
        this.availablePos = new ArrayList<>();
        addBaseSection(BaseSectionTemplates.BASE_CORE, true, new BaseSectPos(0, 0, 0), BlockRotation.NONE);
        baseSectGetAvailablePos();
    }

    /// Gets all the positions adjacent to a base section that itself is not occupied by a section.
    private void baseSectGetAvailablePos(){
        for (BaseSection section : sections) {
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

    /// Grows the base at random by adding an extra base section to the base
    public void growBase(){
        int randomInt = random.nextInt(sectionTemplateList.size());
        BaseSectPos offset = availablePos.toArray(new BaseSectPos[0])[random.nextInt(availablePos.size())];
        BlockRotation rot = BlockRotation.NONE;

        addBaseSection(sectionTemplateList.get(randomInt), false, offset, rot);
    }

    private boolean checkSectionLocationClear(BaseSectPos pos){
        for (BaseSection section : sections) {
            if (section.getOrigin().equals(pos)){
                return false;
            }
        }
        return true;
    }

    private void addBaseSection(BaseSectionTemplate sectionTemplate, boolean isCore, BaseSectPos offset, BlockRotation rot){
        BaseSection newSection = new BaseSection(sectionTemplate, world, offset, rot, false);
        sections.add(newSection);
        availablePos.remove(offset);
        baseSectCheckAdjPos(newSection);

        Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
        bot.ifPresent(x -> {
            x.setSection(newSection);
        });
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

    /// Adds a preexisting builder to the builders
    public void hireBuilder(AlienBuilderBotEntity builder){
        builders.add(builder);
    }

    /// Adds a newly spawned builder to the builders
    public void spawnBuilder(){
        hireBuilder(new AlienBuilderBotEntity(world, this));
    }

    /// Ticks this alien base.
    public void tick(){
        if (random.nextInt(200) == 0) {
            //TODO: Make the base grow, spawning builders as needed.
            Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
            if (bot.isEmpty()) {
                spawnBuilder();
            }
            else {
                growBase();
            }
        }
    }

    public BlockPos getOrigin(){
        return this.origin;
    }

    public ArrayList<BaseSection> getSections(){
        return this.sections;
    }

    public ArrayList<AlienBuilderBotEntity> getBuilders(){
        return this.builders;
    }

    public UUID getUuid(){
        return this.uuid;
    }
}
