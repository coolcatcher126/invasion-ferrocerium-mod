package io.github.coolcatcher126.ferrocerium.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.base.BaseSection;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;
import java.util.Set;

public class NearestChestSensor extends Sensor<AlienBuilderBotEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(ModMemoryModuleTypes.CHEST_LOCATION);
    }

    protected void sense(ServerWorld serverWorld, AlienBuilderBotEntity builderBotEntity) {
        findNearestChest(builderBotEntity, builderBotEntity.getBase());
    }

    private void findNearestChest(AlienBuilderBotEntity entity, AlienBase base) {
        if (base != null) {
            BaseSection section = base.getClosestSectionTo(entity.getBlockPos());
            if (section != null) {
                Optional<BlockPos> optional = section.getChestLocations().stream()
                        .min((pos1, pos2) ->
                                (int) (entity.getBlockPos().getSquaredDistance(pos1) -
                                entity.getBlockPos().getSquaredDistance(pos2))
                        );
                entity.getBrain().remember(ModMemoryModuleTypes.CHEST_LOCATION, optional.map(blockPos -> GlobalPos.create(base.getDimension(), blockPos)));
            }
        }
    }
}
