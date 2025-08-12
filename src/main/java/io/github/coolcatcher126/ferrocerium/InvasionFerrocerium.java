package io.github.coolcatcher126.ferrocerium;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntBotMissileEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntSoldierBotEntity;
import io.github.coolcatcher126.ferrocerium.item.ModItemGroups;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import io.github.coolcatcher126.ferrocerium.network.InvasionStateChangedPayload;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import io.github.coolcatcher126.ferrocerium.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvasionFerrocerium implements ModInitializer {
	public static final String MOD_ID = "invasion-ferrocerium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private int invasionState = 0;

	@Override
	public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(InvasionStateChangedPayload.ID, InvasionStateChangedPayload.CODEC);

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