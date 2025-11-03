package io.github.coolcatcher126.ferrocerium.components;

import io.github.coolcatcher126.ferrocerium.base.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class InvasionLevelComponent implements Component, ServerTickingComponent {
    /// There are 6 stages:
    /// Stage 0 - No invasion
    /// Stage 1 - Scouts land
    /// Stage 2 - Groups of a few soldiers also spawn in larger scouting parties
    /// Stage 3 - A few small bases are scattered around
    /// Stage 4 - One base is made into the main base, a few small bases
    /// Stage 5 - Full on occupation with all enemies spawning. Large bases everywhere.
    public static final int MAX_INVASION = 5;

    //The current invasion state
    private int invasionState = 0;
    // bases that currently exist
    private ArrayList<AlienBase> bases = new ArrayList<>();

    private final World world;

    public InvasionLevelComponent(World world){
        this.world = world;
    }

    public int getInvasionState(){
        return this.invasionState;
    }

    public void fightBackInvasion(){
        this.invasionState = MathHelper.clamp(--this.invasionState, 0, MAX_INVASION);
    }

    public void progressInvasion(){
        this.invasionState = MathHelper.clamp(++this.invasionState, 0, MAX_INVASION);
    }

    public void setInvasion(int invasionState){
        this.invasionState = MathHelper.clamp(invasionState, 0, MAX_INVASION);
    }

    public ArrayList<AlienBase> getBases(){
        return this.bases;
    }

    public void setBases(ArrayList<AlienBase> bases){
        this.bases = bases;
    }

    public void addBase(AlienBase base){
        this.bases.add(base);
    }

    public void removeBase(AlienBase base){
        this.bases.remove(base);
    }

    //The following are for saving data upon level save.

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (this.invasionState > 0) {
            nbtCompound.putInt("invasion_state", this.invasionState);
        }
        ArrayList<AlienBaseSave> baseSaves = new ArrayList<>();
        for (AlienBase base : bases) {
            baseSaves.add(alienBaseSaveFromAlienBase(base));
        }
        saveBaseListData(nbtCompound, baseSaves);

    }

    private static void saveBaseListData(NbtCompound nbtCompound, ArrayList<AlienBaseSave> basesToSave){
        NbtList nbtList = new NbtList();
        for (AlienBaseSave alienBase : basesToSave) {
            nbtList.add(saveAlienBaseSave(alienBase));
        }
        nbtCompound.put("alien_bases", nbtList);
    }

    /// Serialises data stored in the class AlienBaseSave to NBT data.
    private static NbtCompound saveAlienBaseSave(AlienBaseSave alienBase) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("alien_base_z",alienBase.origin.getZ());
        nbtCompound.putInt("alien_base_y",alienBase.origin.getY());
        nbtCompound.putInt("alien_base_x",alienBase.origin.getX());
        NbtList nbtList = new NbtList();
        for (BaseSectionSave section : alienBase.sections) {
            nbtList.add(saveBaseSectionSave(section));
        }
        nbtCompound.put("alien_base_sections", nbtList);
        nbtCompound.putUuid("alien_base_uuid", alienBase.uuid);
        return nbtCompound;
    }

    /// Serialises data stored in the class BaseSectionSave to NBT data.
    private static NbtCompound saveBaseSectionSave(BaseSectionSave baseSection){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("base_section_is_core", baseSection.isCore);
        nbtCompound.putString("base_section_rotation", baseSection.rotation.name());
        nbtCompound.putInt("base_section_z",baseSection.origin.getZ());
        nbtCompound.putInt("base_section_y",baseSection.origin.getY());
        nbtCompound.putInt("base_section_x",baseSection.origin.getX());
        nbtCompound.putString("base_section_name", baseSection.name);
        return nbtCompound;
    }

    //The following are for loading data upon level load.

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        ArrayList<AlienBaseSave> baseSaves = loadBaseListData(nbtCompound);
        bases = new ArrayList<>();
        for (AlienBaseSave baseSave : baseSaves) {
            bases.add(alienBaseFromAlienBaseSave(baseSave));
        }

        if (nbtCompound.contains("invasion_state", NbtElement.INT_TYPE)) {
            this.invasionState = nbtCompound.getInt("invasion_state");
        }
    }

    private ArrayList<AlienBaseSave> loadBaseListData(NbtCompound nbtCompound){
        NbtList nbtList = nbtCompound.getList("alien_bases", NbtElement.COMPOUND_TYPE);
        ArrayList<AlienBaseSave> savedBases = new ArrayList<>();
        for (NbtElement nbtElement : nbtList) {
            if (nbtElement instanceof NbtCompound) {
                savedBases.add(loadAlienBaseSave((NbtCompound) nbtElement));
            }
            else{
                throw new InvalidNbtException("Base data does not exist");
            }
        }
        return savedBases;
    }

    /// Loads data from NBT into the class AlienBaseSave.
    private AlienBaseSave loadAlienBaseSave(NbtCompound nbtCompound) {
        UUID uuid = nbtCompound.getUuid("alien_base_uuid");
        NbtList nbtList = nbtCompound.getList("alien_base_sections", NbtElement.COMPOUND_TYPE);
        ArrayList<BaseSectionSave> savedSections = new ArrayList<>();
        for (NbtElement nbtElement : nbtList) {
            if (nbtElement instanceof NbtCompound){
                savedSections.add(loadBaseSectionSave((NbtCompound) nbtElement));
            }
            else{
                throw new InvalidNbtException("Base data does not exist");
            }
        }
        BlockPos origin = new BlockPos(
                nbtCompound.getInt("alien_base_x"),
                nbtCompound.getInt("alien_base_y"),
                nbtCompound.getInt("alien_base_z"));
        return new AlienBaseSave(origin, savedSections, uuid);
    }

    /// Loads data from NBT into the class BaseSectionSave.
    private BaseSectionSave loadBaseSectionSave(NbtCompound nbtCompound){
        return new BaseSectionSave(
                nbtCompound.getString("base_section_name"),
                new BaseSectPos(
                        nbtCompound.getInt("base_section_x"),
                        nbtCompound.getInt("base_section_y"),
                        nbtCompound.getInt("base_section_z")),
                BlockRotation.valueOf(nbtCompound.getString("base_section_rotation")),
                nbtCompound.getBoolean("base_section_is_core"));
    }

    /// Gets the AlienBaseSave from the Alien Base
    public AlienBaseSave alienBaseSaveFromAlienBase(AlienBase alienBase){
        ArrayList<BaseSectionSave> sections = new ArrayList<>();
        for (BaseSection section : alienBase.getSections()) {
            sections.add(baseSectionSaveFromBaseSection(section));
        }
        return new AlienBaseSave(alienBase.getOrigin(), sections, alienBase.getUuid());
    }

    /// Gets the BaseSectionSave from the BaseSection data.
    public BaseSectionSave baseSectionSaveFromBaseSection(BaseSection baseSection){
        return new BaseSectionSave(baseSection.getTemplateName(), baseSection.getOrigin(), baseSection.getRotation(), baseSection.getIsCore());
    }

    /// Gets the AlienBase from the AlienBaseSave
    public AlienBase alienBaseFromAlienBaseSave(AlienBaseSave alienBaseSave){
        ArrayList<BaseSection> sections = new ArrayList<>();
        for (BaseSectionSave section : alienBaseSave.sections) {
            sections.add(baseSectionFromBaseSectionSave(section));
        }
        return  new AlienBase(world, alienBaseSave.origin, sections, new ArrayList<AlienBuilderBotEntity>(), alienBaseSave.uuid);
    }

    /// Gets the BaseSection from the BaseSectionSave
    public BaseSection baseSectionFromBaseSectionSave(BaseSectionSave baseSectionSave){
        BaseSectionTemplate template;
        for (BaseSectionTemplate sectionTemplate : InvasionFerroceriumRegistries.BASE_SECTION) {
            if (Objects.equals(sectionTemplate.name, baseSectionSave.name)) {
                template = sectionTemplate;
                return new BaseSection(template, world, baseSectionSave.origin, baseSectionSave.rotation, baseSectionSave.isCore);
            }
        }
        return null;
    }

    @Override
    public void serverTick() {
        for (AlienBase base : bases) {
            base.tick();
        }
    }
}
