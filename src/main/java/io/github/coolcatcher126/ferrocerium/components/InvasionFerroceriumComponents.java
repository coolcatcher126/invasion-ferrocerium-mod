package io.github.coolcatcher126.ferrocerium.components;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class InvasionFerroceriumComponents implements WorldComponentInitializer {
    public static final ComponentKey<InvasionLevelComponent> INVASION_LEVEL = ComponentRegistry.getOrCreate(
            Identifier.of(InvasionFerrocerium.MOD_ID, "invasion_state"),
            InvasionLevelComponent.class
    );

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(INVASION_LEVEL, it -> new InvasionLevelComponent());
    }

    public static int getInvasionLevel(World world){
        return INVASION_LEVEL.get(world).getInvasionState();
    }
}