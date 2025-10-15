package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

/// A copy of the data stored in Alien Base in a form that is more easily serialised into NBT data.
public class AlienBaseSave {

    public BlockPos origin;
    public ArrayList<BaseSectionSave> sections;
    public UUID uuid;

    public AlienBaseSave(BlockPos origin, ArrayList<BaseSectionSave> sections, UUID uuid){
        this.origin = origin;
        this.sections = sections;
        this.uuid = uuid;
    }
}
