package io.github.coolcatcher126.ferrocerium.components;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

import java.util.Optional;

public class InvasionFerroceriumComponents implements WorldComponentInitializer {
    public static final ComponentKey<InvasionLevelComponent> INVASION_LEVEL = ComponentRegistry.getOrCreate(
            Identifier.of(InvasionFerrocerium.MOD_ID, "invasion_state"),
            InvasionLevelComponent.class
    );

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(INVASION_LEVEL, it -> new InvasionLevelComponent());
    }

    public static Optional<InvasionLevelComponent> getInvasionLevelComponent(World world){
        if(world == null) {
            return Optional.empty();
        }
        return Optional.of(world.getComponent(INVASION_LEVEL));
    }

    public static int getInvasionLevel(World world){
        return world.getComponent(INVASION_LEVEL).getInvasionState();
    }

    public static void fightBackInvasion(World world){
        world.getComponent(INVASION_LEVEL).fightBackInvasion();
    }

    public static void progressInvasion(World world){
        world.getComponent(INVASION_LEVEL).progressInvasion();
    }

    public static void setInvasion(World world, int invasionState){
        world.getComponent(INVASION_LEVEL).setInvasion(invasionState);
    }
}