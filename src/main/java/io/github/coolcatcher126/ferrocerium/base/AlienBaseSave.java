package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/// A copy of the data stored in Alien Base in a form that is more easily serialised into NBT data.
public class AlienBaseSave {

    public BlockPos origin;
    public ArrayList<BaseSectionSave> sections;
    public ArrayList<UUID> builders;
    public ArrayList<BaseBlock> baseBlocks;
    public ArrayList<Vein> resources;
    public UUID uuid;

    public AlienBaseSave(BlockPos origin, ArrayList<BaseSectionSave> sections, ArrayList<UUID> builders, ArrayList<BaseBlock> baseBlocks, ArrayList<Vein> resources, UUID uuid){
        this.origin = origin;
        this.sections = sections;
        this.builders = builders;
        this.baseBlocks = baseBlocks;
        this.resources = resources;
        this.uuid = uuid;
    }
}
