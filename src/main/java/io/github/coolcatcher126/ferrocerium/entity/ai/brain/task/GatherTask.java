package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Optional;

public class GatherTask extends MultiTickTask<AlienBuilderBotEntity> {


    public GatherTask() {
        super(Map.of(ModMemoryModuleTypes.BASE_SECTION_LOCATION, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        throw  new NotImplementedException();
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if there is a vein to be collected
        throw  new NotImplementedException();
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Mine the required blocks one block at a time
        throw  new NotImplementedException();
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }
}
