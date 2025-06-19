package io.github.coolcatcher126.ferrocerium.util;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_STRANGE_ALLOY_TOOL = createTag("needs_strange_alloy_tool");
        public static final TagKey<Block> INCORRECT_FOR_STRANGE_ALLOY = createTag("incorrect_for_strange_alloy_tool");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(InvasionFerrocerium.MOD_ID, name));
        }
    }
}
