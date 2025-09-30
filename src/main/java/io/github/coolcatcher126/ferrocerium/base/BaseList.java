package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;

/// A storage of alien bases
public class BaseList {
    // bases that currently exist serverside
    private static final ArrayList<AlienBase> bases = new ArrayList<>();

    public static void registerAlienBaseList(){
        InvasionFerrocerium.LOGGER.info("Registering alien base list " + InvasionFerrocerium.MOD_ID);

        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            for (AlienBase base : bases) {
                base.tick();
            }
        });
    }
}
