package io.github.coolcatcher126.ferrocerium.block;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {
    public static final Block ALUMINUM_BLOCK = registerBlock("aluminum_block", new Block(AbstractBlock.Settings.create()
            .strength(4f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
    ));
    public static final Block RAW_ALUMINUM_BLOCK = registerBlock("raw_aluminum_block", new Block(AbstractBlock.Settings.create()
            .strength(3f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
    ));
    public static final Block ALUMINUM_ORE_BLOCK = registerBlock("aluminum_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
            AbstractBlock.Settings.create()
            .strength(3f)
            .requiresTool()
            .sounds(BlockSoundGroup.STONE)
    ));
    public static final Block DEEPSLATE_ALUMINUM_ORE_BLOCK = registerBlock("deepslate_aluminum_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 6),
            AbstractBlock.Settings.create()
            .strength(4f)
            .requiresTool()
            .sounds(BlockSoundGroup.DEEPSLATE)
    ));
    public static final Block STRANGE_ALLOY_BLOCK = registerBlock("strange_alloy_block", new Block(AbstractBlock.Settings.create()
            .strength(5f)
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
    }
}
