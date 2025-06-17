package io.github.coolcatcher126.ferrocerium.item;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item THERMITE = registerItem("thermite", new Item(new Item.Settings()));
    public static final Item ALUMINUM_INGOT = registerItem("aluminum_ingot", new Item(new Item.Settings()));
    public static final Item RAW_ALUMINUM = registerItem("raw_aluminum", new Item(new Item.Settings()));
    public static final Item STRANGE_ALLOY_INGOT = registerItem("strange_alloy_ingot", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(InvasionFerrocerium.MOD_ID, name), item);
    }

    public static void registerModItems(){
        InvasionFerrocerium.LOGGER.info("Registering mod items for " + InvasionFerrocerium.MOD_ID);
    }
}
