package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.sections.BaseFactoryTemplate;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import io.github.coolcatcher126.ferrocerium.base.sections.BaseCoreTemplate;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BaseSectionTemplates {
    public static final BaseCoreTemplate BASE_CORE = Registry.register(InvasionFerroceriumRegistries.BASE_SECTION,
            Identifier.of(InvasionFerrocerium.MOD_ID, "base_core"),
            new BaseCoreTemplate());
    public static final BaseFactoryTemplate BASE_FACTORY = Registry.register(InvasionFerroceriumRegistries.BASE_SECTION,
            Identifier.of(InvasionFerrocerium.MOD_ID, "base_factory"),
            new BaseFactoryTemplate());


    public static void registerBaseSections(){
        InvasionFerrocerium.LOGGER.info("Registering base sections for " + InvasionFerrocerium.MOD_ID);
    }
}
