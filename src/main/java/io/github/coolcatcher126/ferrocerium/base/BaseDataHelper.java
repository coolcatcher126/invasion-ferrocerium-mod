package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.*;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Optional;


//A class that holds static helper functions relating to robot base building
public class BaseDataHelper {

    public static AlienBaseSave alienBaseSaveFromAlienBase(AlienBase alienBase){
        //TODO: Get the AlienBaseSave from the AlienBase data.
        throw new NotImplementedException();

    }

    public static AlienBase alienBaseFromAlienBaseSave(AlienBaseSave alienBaseSave){
        //TODO: Get the AlienBase from the AlienBaseSave
        throw new NotImplementedException();
    }

    public static BaseSectionSave baseSectionSaveFromBaseSection(BaseSection baseSection){
        //TODO: Get the BaseSectionSave from the BaseSection data.
        throw new NotImplementedException();
        BaseSectionSave baseSectionSave = new BaseSectionSave();
        return baseSectionSave;
    }

    public static BaseSection baseSectionFromBaseSectionSave(BaseSectionSave baseSectionSave){
        //TODO: Get the template and world from the BaseSectionSave
        throw new NotImplementedException();
        BaseSection baseSection = new BaseSection(template, world, baseSectionSave.origin, baseSectionSave.rotation, baseSectionSave.isCore);
        return baseSection;
    }

    public static ArrayList<BaseBlock> getBaseBlocksFromNbt(String structureName, World world) {
        ResourceManager resourceManager;
        if (world.isClient())
            resourceManager = MinecraftClient.getInstance().getResourceManager();
        else
            resourceManager = world.getServer().getResourceManager();

        NbtCompound nbt = getBuildingNbt(structureName, resourceManager);
        ArrayList<BaseBlock> blocks = new ArrayList<>();

        // load in blocks (list of blockPos and their palette index)
        NbtList blocksNbt = nbt.getList("blocks", 10);

        return getBaseBlocksFromNbt(nbt);
    }

    public static ArrayList<BaseBlock> getBaseBlocksFromNbt(NbtCompound nbt) {
        ArrayList<BaseBlock> blocks = new ArrayList<>();

        // load in blocks (list of blockPos and their palette index)
        NbtList blocksNbt = nbt.getList("blocks", 10);

        ArrayList<BlockState> palette = getBuildingPalette(nbt);

        for(int i = 0; i < blocksNbt.size(); i++) {
            NbtCompound blockNbt = blocksNbt.getCompound(i);
            NbtList blockPosNbt = blockNbt.getList("pos", 3);

            BlockPos bp = new BlockPos(
                    blockPosNbt.getInt(0),
                    blockPosNbt.getInt(1),
                    blockPosNbt.getInt(2)
            );
            BlockState bs = palette.get(blockNbt.getInt("state"));

            if (bs.getBlock() != Blocks.WATER || bs.getFluidState().isStill())
                blocks.add(new BaseBlock(bp, bs));
        }
        return blocks;
    }

    public static NbtCompound getBuildingNbt(String structureName, ResourceManager resManager) {
        try {
            Identifier rl = Identifier.of("invasion-ferrocerium", "structures/" + structureName + ".nbt");
            Optional<Resource> rs = resManager.getResource(rl);
            return NbtIo.readCompressed(rs.get().getInputStream(), NbtSizeTracker.ofUnlimitedBytes());
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static ArrayList<BlockState> getBuildingPalette(NbtCompound nbt) {
        ArrayList<BlockState> palette = new ArrayList<>();
        // load in palette (list of unique blockstates)
        NbtList paletteNbt = nbt.getList("palette", 10);
        for(int i = 0; i < paletteNbt.size(); i++)
            palette.add(NbtHelper.toBlockState(BuiltinRegistries.createWrapperLookup().getWrapperOrThrow(RegistryKeys.BLOCK), paletteNbt.getCompound(i)));
        return palette;
    }
}
