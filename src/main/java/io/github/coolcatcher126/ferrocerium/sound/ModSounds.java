package io.github.coolcatcher126.ferrocerium.sound;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent ANT_SCOUT_BOT_AMBIENT = registerSound("ant_scout_bot_ambient");
    public static final SoundEvent ANT_SCOUT_BOT_HURT = registerSound("ant_scout_bot_hurt");
    public static final SoundEvent ANT_SCOUT_BOT_DEATH = registerSound("ant_scout_bot_death");
    public static final SoundEvent ANT_SCOUT_BOT_STEP = registerSound("ant_scout_bot_step");

    private static SoundEvent registerSound(String name) {
        Identifier identifier = Identifier.of(InvasionFerrocerium.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }


    public static void registerModSounds() {
        InvasionFerrocerium.LOGGER.info("Registering " + InvasionFerrocerium.MOD_ID + " Sounds");
    }
}
