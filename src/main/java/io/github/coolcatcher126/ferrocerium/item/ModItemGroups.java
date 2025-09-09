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
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.THERMITE))
                    .displayName(Text.translatable("itemgroup.invasion-ferrocerium.invasion_ferrocerium_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.THERMITE);
                        entries.add(ModItems.ALUMINUM_INGOT);
                        entries.add(ModItems.RAW_ALUMINUM);
                        entries.add(ModItems.STRANGE_ALLOY_INGOT);
                        entries.add(ModItems.ALIEN_CIRCUITS);
                        entries.add(ModItems.ALIEN_POWER_CELL);
                        entries.add(ModItems.ALIEN_TRANSCEIVER);
                        entries.add(ModItems.ALIEN_BEACON);
                        entries.add(ModItems.CRUDE_ALIEN_BEACON);
                        entries.add(ModItems.STRANGE_ALLOY_SWORD);
                        entries.add(ModItems.STRANGE_ALLOY_PICKAXE);
                        entries.add(ModItems.STRANGE_ALLOY_AXE);
                        entries.add(ModItems.STRANGE_ALLOY_SHOVEL);
                        entries.add(ModItems.STRANGE_ALLOY_HOE);
                        entries.add(ModItems.ANT_SCOUT_BOT_SPAWN_EGG);
                        entries.add(ModItems.ANT_SOLDIER_BOT_SPAWN_EGG);
                        entries.add(ModItems.ALIEN_HELICOPTER_BOT_SPAWN_EGG);
                        entries.add(ModItems.ALIEN_BUILDER_BOT_SPAWN_EGG);
                    }).build());

    public static final ItemGroup FERROCERIUM_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ferrocerium_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.STRANGE_ALLOY_BLOCK))
                    .displayName(Text.translatable("itemgroup.invasion-ferrocerium.invasion_ferrocerium_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.ALUMINUM_BLOCK);
                        entries.add(ModBlocks.RAW_ALUMINUM_BLOCK);
                        entries.add(ModBlocks.STRANGE_ALLOY_BLOCK);
                        entries.add(ModBlocks.ALUMINUM_ORE_BLOCK);
                        entries.add(ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK);
                    }).build());


    public static void registerItemGroups() {
        InvasionFerrocerium.LOGGER.info("Registering Item Groups for " + InvasionFerrocerium.MOD_ID);
    }
}
