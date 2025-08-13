package io.github.coolcatcher126.ferrocerium;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntBotMissileEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntSoldierBotEntity;
import io.github.coolcatcher126.ferrocerium.item.ModItemGroups;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import io.github.coolcatcher126.ferrocerium.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvasionFerrocerium implements ModInitializer {
	public static final String MOD_ID = "invasion-ferrocerium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModSounds.registerModSounds();
		ModEntities.registerModEntities();

		ModWorldGeneration.GenerateModWorldGen();
		
		FabricDefaultAttributeRegistry.register(ModEntities.ANT_SCOUT_BOT, AntScoutBotEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANT_SOLDIER_BOT, AntSoldierBotEntity.createAttributes());
	}
}