package io.github.coolcatcher126.ferrocerium.entity.ai.brain;

import com.mojang.serialization.Codec;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public class ModMemoryModuleTypes {
    public static final MemoryModuleType<GlobalPos> BASE_SECTION_LOCATION = register("base_section_location");
    public static final MemoryModuleType<GlobalPos> RESOURCE_LOCATION = register("resource_location");
    public static final MemoryModuleType<Boolean> BUILDING = register("building");
    public static final MemoryModuleType<Boolean> GATHERING = register("gathering");
    public static final MemoryModuleType<Boolean> MINING = register("mining");
    public static final MemoryModuleType<Boolean> CRAFTING = register("crafting");

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
