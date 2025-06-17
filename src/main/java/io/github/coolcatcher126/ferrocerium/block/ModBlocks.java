package io.github.coolcatcher126.ferrocerium.block;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block FERROCEROUS_BLOCK = registerBlock("ferrocerous_block", new Block(AbstractBlock.Settings.create()
            .strength(1f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            ));
    public static final Block CERIUM_BLOCK = registerBlock("cerium_block", new Block(AbstractBlock.Settings.create()
            .strength(1f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
    ));
    public static final Block RAW_CERIUM_BLOCK = registerBlock("raw_cerium_block", new Block(AbstractBlock.Settings.create()
            .strength(1f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
    ));
    public static final Block STRANGE_ALLOY_BLOCK = registerBlock("strange_alloy_block", new Block(AbstractBlock.Settings.create()
            .strength(1f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
    ));

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(InvasionFerrocerium.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, Identifier.of(InvasionFerrocerium.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }



    public static void registerModBlocks(){
        InvasionFerrocerium.LOGGER.info("Registering mod blocks for " + InvasionFerrocerium.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(FERROCEROUS_BLOCK);
            fabricItemGroupEntries.add(CERIUM_BLOCK);
            fabricItemGroupEntries.add(RAW_CERIUM_BLOCK);
            fabricItemGroupEntries.add(STRANGE_ALLOY_BLOCK);
        });
    }
}
