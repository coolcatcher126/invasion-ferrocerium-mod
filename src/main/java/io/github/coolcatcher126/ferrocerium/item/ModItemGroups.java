package io.github.coolcatcher126.ferrocerium.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup FERROCERIUM_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ferrocerium_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.FERROCEROUS_INGOT))
                    .displayName(Text.translatable("itemgroup.invasion-ferrocerium.invasion_ferrocerium_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.FERROCEROUS_INGOT);
                        entries.add(ModItems.CERIUM_INGOT);
                        entries.add(ModItems.RAW_CERIUM);
                        entries.add(ModItems.STRANGE_ALLOY_INGOT);
                    }).build());

    public static final ItemGroup FERROCERIUM_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ferrocerium_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.FERROCEROUS_BLOCK))
                    .displayName(Text.translatable("itemgroup.invasion-ferrocerium.invasion_ferrocerium_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.FERROCEROUS_BLOCK);
                        entries.add(ModBlocks.CERIUM_BLOCK);
                        entries.add(ModBlocks.RAW_CERIUM_BLOCK);
                        entries.add(ModBlocks.STRANGE_ALLOY_BLOCK);
                    }).build());


    public static void registerItemGroups() {
        InvasionFerrocerium.LOGGER.info("Registering Item Groups for " + InvasionFerrocerium.MOD_ID);
    }
}
