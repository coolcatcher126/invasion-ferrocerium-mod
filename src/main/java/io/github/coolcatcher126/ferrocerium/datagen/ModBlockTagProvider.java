package io.github.coolcatcher126.ferrocerium.datagen;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.ALUMINUM_BLOCK)
                .add(ModBlocks.RAW_ALUMINUM_BLOCK)
                .add(ModBlocks.ALUMINUM_ORE_BLOCK)
                .add(ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK)
                .add(ModBlocks.STRANGE_ALLOY_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ALUMINUM_BLOCK)
                .add(ModBlocks.RAW_ALUMINUM_BLOCK)
                .add(ModBlocks.ALUMINUM_ORE_BLOCK)
                .add(ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.STRANGE_ALLOY_BLOCK);

        getOrCreateTagBuilder(ModTags.Blocks.NEEDS_STRANGE_ALLOY_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL);
    }
}
