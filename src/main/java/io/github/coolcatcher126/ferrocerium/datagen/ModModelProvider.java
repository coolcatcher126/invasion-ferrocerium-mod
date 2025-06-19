package io.github.coolcatcher126.ferrocerium.datagen;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ALUMINUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_ALUMINUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ALUMINUM_ORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STRANGE_ALLOY_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.THERMITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALUMINUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_ALUMINUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRANGE_ALLOY_INGOT, Models.GENERATED);
    }
}
