package io.github.coolcatcher126.ferrocerium.entity;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntBotMissileEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntScoutBotEntity;
import io.github.coolcatcher126.ferrocerium.entity.custom.AntSoldierBotEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<AntBotMissileEntity> ANT_BOT_MISSILE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ant_bot_missile"),
            EntityType.Builder.<AntBotMissileEntity>create(AntBotMissileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<AntScoutBotEntity> ANT_SCOUT_BOT = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ant_scout_bot"),
            EntityType.Builder.create(AntScoutBotEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1F, 1F).build());

    public static final EntityType<AntSoldierBotEntity> ANT_SOLDIER_BOT = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(InvasionFerrocerium.MOD_ID, "ant_soldier_bot"),
            EntityType.Builder.create(AntSoldierBotEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.5F, 1.5F).build());

    public static void registerModEntities(){
        InvasionFerrocerium.LOGGER.info("Registering Mod Entities for: " + InvasionFerrocerium.MOD_ID);
    }
}
