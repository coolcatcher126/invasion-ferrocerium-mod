package io.github.coolcatcher126.ferrocerium.registries;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.BaseSectionTemplate;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class InvasionFerroceriumRegistries {

    public static final RegistryKey<Registry<BaseSectionTemplate>> BASE_SECTION_KEY = RegistryKey.ofRegistry(Identifier.of(InvasionFerrocerium.MOD_ID, "base_sections"));

    public static Registry<BaseSectionTemplate> BASE_SECTION;

    public static void buildRegistries() {
        InvasionFerrocerium.LOGGER.info("Building registries for " + InvasionFerrocerium.MOD_ID);
        BASE_SECTION = FabricRegistryBuilder.createSimple(
                        InvasionFerroceriumRegistries.BASE_SECTION_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }

}
