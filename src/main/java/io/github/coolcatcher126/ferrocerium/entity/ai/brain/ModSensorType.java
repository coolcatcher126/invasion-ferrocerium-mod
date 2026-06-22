package io.github.coolcatcher126.ferrocerium.entity.ai.brain;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.sensor.NearestChestSensor;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.sensor.DummySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModSensorType {
    public static final SensorType<NearestChestSensor> NEAREST_CHEST = register("nearest_chest", NearestChestSensor::new);

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(Registries.SENSOR_TYPE, Identifier.ofVanilla(id), new SensorType<>(factory));
    }
}
