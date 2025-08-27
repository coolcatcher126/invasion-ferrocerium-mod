package io.github.coolcatcher126.ferrocerium.datagen;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;

import java.util.Optional;

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
        itemModelGenerator.register(ModItems.ALIEN_CIRCUITS, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALIEN_POWER_CELL, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALIEN_TRANSCEIVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALIEN_BEACON, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRUDE_ALIEN_BEACON, Models.GENERATED);

        itemModelGenerator.register(ModItems.STRANGE_ALLOY_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.STRANGE_ALLOY_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.STRANGE_ALLOY_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.STRANGE_ALLOY_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.STRANGE_ALLOY_HOE, Models.HANDHELD);

        itemModelGenerator.register(ModItems.ANT_SCOUT_BOT_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.ANT_SOLDIER_BOT_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
    }
}
