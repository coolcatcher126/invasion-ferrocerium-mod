package io.github.coolcatcher126.ferrocerium.datagen;

import io.github.coolcatcher126.ferrocerium.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.SWORDS).add(ModItems.STRANGE_ALLOY_SWORD);
        getOrCreateTagBuilder(ItemTags.PICKAXES).add(ModItems.STRANGE_ALLOY_PICKAXE);
        getOrCreateTagBuilder(ItemTags.AXES).add(ModItems.STRANGE_ALLOY_AXE);
        getOrCreateTagBuilder(ItemTags.SHOVELS).add(ModItems.STRANGE_ALLOY_SHOVEL);
        getOrCreateTagBuilder(ItemTags.HOES).add(ModItems.STRANGE_ALLOY_HOE);
    }
}
