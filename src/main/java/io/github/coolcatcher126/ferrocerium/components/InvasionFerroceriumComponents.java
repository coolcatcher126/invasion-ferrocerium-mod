package io.github.coolcatcher126.ferrocerium.components;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class InvasionFerroceriumComponents implements WorldComponentInitializer {
    public static final ComponentKey<InvasionLevelComponent> INVASION_LEVEL = ComponentRegistry.getOrCreate(
            Identifier.of("ferrocerium", "invasion_state"),
            InvasionLevelComponent.class
    );

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(INVASION_LEVEL, it -> new InvasionLevelComponent());
    }
}