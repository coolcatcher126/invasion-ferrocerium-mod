package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

/// A copy of the data stored in Alien Base in a form that is more easily serialised into NBT data.
public class AlienBaseSave {

    public BlockPos origin;
    public ArrayList<BaseSectionSave> sections;
    public ArrayList<BaseBlock> baseBlocks;
    public ArrayList<Vein> resources;
    public UUID uuid;
    public int baseGrowTime;
    public int search_time_count;

    public AlienBaseSave(BlockPos origin, ArrayList<BaseSectionSave> sections, ArrayList<BaseBlock> baseBlocks, ArrayList<Vein> resources, UUID uuid, int baseGrowTime, int search_time_count){
        this.origin = origin;
        this.sections = sections;
        this.baseBlocks = baseBlocks;
        this.resources = resources;
        this.uuid = uuid;
        this.baseGrowTime = baseGrowTime;
        this.search_time_count = search_time_count;
    }
}
