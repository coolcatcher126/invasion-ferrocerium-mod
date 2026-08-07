package io.github.coolcatcher126.ferrocerium.entity.ai.brain;

import com.mojang.serialization.Codec;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.GlobalPos;

import java.util.List;
import java.util.Optional;

public class ModMemoryModuleTypes {
    public static final MemoryModuleType<GlobalPos> BASE_SECTION_LOCATION = register("base_section_location");
    public static final MemoryModuleType<GlobalPos> RESOURCE_LOCATION = register("resource_location");
    public static final MemoryModuleType<GlobalPos> CHEST_LOCATION = register("chest_location");
    public static final MemoryModuleType<List<BaseBlock>> BUILDING = register("building");
    public static final MemoryModuleType<Unit> GATHERING = register("gathering", Unit.CODEC);
    public static final MemoryModuleType<Unit> MINING = register("mining", Unit.CODEC);
    public static final MemoryModuleType<Unit> EXCHANGING = register("exchanging", Unit.CODEC);
    public static final MemoryModuleType<Unit> BUILD_SITE_CLEAR = register("build_site_clear", Unit.CODEC);
    public static final MemoryModuleType<Integer> ACTIVITY_TICKS = register("activity_ticks");

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.ofVanilla(id), new MemoryModuleType<>(Optional.of(codec)));
    }

    private static <U> MemoryModuleType<U> register(String id) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.ofVanilla(id), new MemoryModuleType<>(Optional.empty()));
    }

    public static void registerModActivities(){
        InvasionFerrocerium.LOGGER.info("Registering mod memory module types for " + InvasionFerrocerium.MOD_ID);
    }
}
