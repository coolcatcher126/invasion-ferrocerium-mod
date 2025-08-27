package io.github.coolcatcher126.ferrocerium.world.gen;

import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntSoldierBotEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Heightmap;

public class ModEntitySpawn {
    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.all(),
                SpawnGroup.MONSTER, ModEntities.ANT_SCOUT_BOT, 10, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.all(),
                SpawnGroup.MONSTER, ModEntities.ANT_SOLDIER_BOT, 10, 1, 1);

        SpawnRestriction.register(ModEntities.ANT_SCOUT_BOT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AntScoutBotEntity::canSpawnInDark);
        SpawnRestriction.register(ModEntities.ANT_SOLDIER_BOT, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AntSoldierBotEntity::canSpawnInDark);
    }
}
