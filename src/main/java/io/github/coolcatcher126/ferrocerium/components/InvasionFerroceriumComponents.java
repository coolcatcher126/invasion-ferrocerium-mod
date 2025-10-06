package io.github.coolcatcher126.ferrocerium.components;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

import java.util.ArrayList;
import java.util.Optional;

public class InvasionFerroceriumComponents implements WorldComponentInitializer {
    public static final ComponentKey<InvasionLevelComponent> INVASION_LEVEL = ComponentRegistry.getOrCreate(
            Identifier.of(InvasionFerrocerium.MOD_ID, "invasion_state"),
            InvasionLevelComponent.class
    );

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(INVASION_LEVEL, InvasionLevelComponent::new);
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

    public static ArrayList<AlienBase> getAlienBases(World world) {
        return  world.getComponent(INVASION_LEVEL).getBases();
    }

    public static void setAlienBases(World world, ArrayList<AlienBase> bases){
        world.getComponent(INVASION_LEVEL).setBases(bases);
    }

    public static void addAlienBase(World world, AlienBase base){
        world.getComponent(INVASION_LEVEL).addBase(base);
    }

    public static void removeAlienBase(World world, AlienBase base){
        world.getComponent(INVASION_LEVEL).removeBase(base);
    }
}