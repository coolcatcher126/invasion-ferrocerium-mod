package io.github.coolcatcher126.ferrocerium.entity.ai.brain;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModActivities {
    public static final Activity BUILD = register("build");
    public static final Activity MINE = register("mine");
    public static final Activity CHOP_WOOD = register("chop_wood");
    public static final Activity CRAFT = register("craft");
    public static final Activity EXCHANGE = register("exchange");

    private static Activity register(String id) {
        return Registry.register(Registries.ACTIVITY, id, new Activity(id));
    }

    public static void registerModActivities(){
        InvasionFerrocerium.LOGGER.info("Registering mod activities for " + InvasionFerrocerium.MOD_ID);
    }
}
