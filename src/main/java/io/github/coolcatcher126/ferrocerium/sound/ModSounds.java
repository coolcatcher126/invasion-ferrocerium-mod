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

    public static final SoundEvent ANT_SOLDIER_BOT_AMBIENT = registerSound("ant_soldier_bot_ambient");
    public static final SoundEvent ANT_SOLDIER_BOT_HURT = registerSound("ant_soldier_bot_hurt");
    public static final SoundEvent ANT_SOLDIER_BOT_DEATH = registerSound("ant_soldier_bot_death");
    public static final SoundEvent ANT_SOLDIER_BOT_STEP = registerSound("ant_soldier_bot_step");

    public static final SoundEvent ALIEN_BUILDER_BOT_AMBIENT = registerSound("alien_builder_bot_ambient");
    public static final SoundEvent ALIEN_BUILDER_BOT_HURT = registerSound("alien_builder_bot_hurt");
    public static final SoundEvent ALIEN_BUILDER_BOT_DEATH = registerSound("alien_builder_bot_death");
    public static final SoundEvent ALIEN_BUILDER_BOT_STEP = registerSound("alien_builder_bot_step");

    public static final SoundEvent ALIEN_HELICOPTER_BOT_AMBIENT = registerSound("alien_helicopter_bot_ambient");
    public static final SoundEvent ALIEN_HELICOPTER_BOT_HURT = registerSound("alien_helicopter_bot_hurt");
    public static final SoundEvent ALIEN_HELICOPTER_BOT_DEATH = registerSound("alien_helicopter_bot_death");

    private static SoundEvent registerSound(String name) {
        Identifier identifier = Identifier.of(InvasionFerrocerium.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }


    public static void registerModSounds() {
        InvasionFerrocerium.LOGGER.info("Registering " + InvasionFerrocerium.MOD_ID + " Sounds");
    }
}
