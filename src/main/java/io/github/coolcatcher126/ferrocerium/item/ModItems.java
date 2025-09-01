package io.github.coolcatcher126.ferrocerium.item;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.item.custom.AlienBeaconItem;
import io.github.coolcatcher126.ferrocerium.item.custom.CrudeAlienBeaconItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    //Regular items
    public static final Item THERMITE = registerItem("thermite", new Item(new Item.Settings()));
    public static final Item ALUMINUM_INGOT = registerItem("aluminum_ingot", new Item(new Item.Settings()));
    public static final Item RAW_ALUMINUM = registerItem("raw_aluminum", new Item(new Item.Settings()));
    public static final Item STRANGE_ALLOY_INGOT = registerItem("strange_alloy_ingot", new Item(new Item.Settings()));

    //Invasion items
    public static final Item ALIEN_CIRCUITS = registerItem("alien_circuits", new Item(new Item.Settings()));;
    public static final Item ALIEN_POWER_CELL = registerItem("alien_power_cell", new Item(new Item.Settings()));;
    public static final Item ALIEN_TRANSCEIVER = registerItem("alien_transceiver", new Item(new Item.Settings()));;
    public static final Item ALIEN_BEACON = registerItem("alien_beacon", new AlienBeaconItem(new Item.Settings()));;
    public static final Item CRUDE_ALIEN_BEACON = registerItem("crude_alien_beacon", new CrudeAlienBeaconItem(new Item.Settings()));;

    //Tools
    public static final Item STRANGE_ALLOY_SWORD = registerItem("strange_alloy_sword",
            new SwordItem(ModToolMaterials.STRANGE_ALLOY, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.STRANGE_ALLOY, 3, -2.4F))));
    public static final Item STRANGE_ALLOY_PICKAXE = registerItem("strange_alloy_pickaxe",
            new PickaxeItem(ModToolMaterials.STRANGE_ALLOY, new Item.Settings().attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.STRANGE_ALLOY, 1, -2.0F))));
    public static final Item STRANGE_ALLOY_AXE = registerItem("strange_alloy_axe",
            new AxeItem(ModToolMaterials.STRANGE_ALLOY, new Item.Settings().attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.STRANGE_ALLOY, 6, -3.2F))));
    public static final Item STRANGE_ALLOY_SHOVEL = registerItem("strange_alloy_shovel",
            new ShovelItem(ModToolMaterials.STRANGE_ALLOY, new Item.Settings().attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.STRANGE_ALLOY, 1.5F, -3.0F))));
    public static final Item STRANGE_ALLOY_HOE = registerItem("strange_alloy_hoe",
            new HoeItem(ModToolMaterials.STRANGE_ALLOY, new Item.Settings().attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.STRANGE_ALLOY, 0, -3.0F))));

    //Spawn eggs
    public static final Item ANT_SCOUT_BOT_SPAWN_EGG = registerItem("ant_scout_bot_spawn_egg",
            new SpawnEggItem(ModEntities.ANT_SCOUT_BOT, 0xff0000, 0x0000ff, new Item.Settings()));
    public static final Item ANT_SOLDIER_BOT_SPAWN_EGG = registerItem("ant_soldier_bot_spawn_egg",
            new SpawnEggItem(ModEntities.ANT_SOLDIER_BOT, 0x0000ff, 0xff0000, new Item.Settings()));
    public static final Item ALIEN_HELICOPTER_BOT_SPAWN_EGG = registerItem("alien_helicopter_bot_spawn_egg",
            new SpawnEggItem(ModEntities.ALIEN_HELICOPTER_BOT, 0x0000ff, 0x000000, new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(InvasionFerrocerium.MOD_ID, name), item);
    }

    public static void registerModItems(){
        InvasionFerrocerium.LOGGER.info("Registering mod items for " + InvasionFerrocerium.MOD_ID);
    }
}
