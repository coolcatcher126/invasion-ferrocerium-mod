package io.github.coolcatcher126.ferrocerium.item;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FERROCEROUS_INGOT = registerItem("ferrocerous_ingot", new Item(new Item.Settings()));
    public static final Item CERIUM_INGOT = registerItem("cerium_ingot", new Item(new Item.Settings()));
    public static final Item RAW_CERIUM = registerItem("raw_cerium", new Item(new Item.Settings()));
    public static final Item STRANGE_ALLOY_INGOT = registerItem("strange_alloy_ingot", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(InvasionFerrocerium.MOD_ID, name), item);
    }

    public static void registerModItems(){
        InvasionFerrocerium.LOGGER.info("Registering mod items for " + InvasionFerrocerium.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(FERROCEROUS_INGOT);
            fabricItemGroupEntries.add(CERIUM_INGOT);
            fabricItemGroupEntries.add(RAW_CERIUM);
            fabricItemGroupEntries.add(STRANGE_ALLOY_INGOT);
        });
    }
}
