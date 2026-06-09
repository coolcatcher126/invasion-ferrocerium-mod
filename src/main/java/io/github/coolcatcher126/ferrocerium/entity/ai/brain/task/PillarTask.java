package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;

public class PillarTask extends MultiTickTask<AlienBuilderBotEntity> {


    public PillarTask() {
        super(Map.of(
                ModMemoryModuleTypes.GATHERING_TICKS, MemoryModuleState.VALUE_PRESENT
                )
        );
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
//        if (!(alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.MINING).get() || alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.GATHERING).get())){
//            return false;
//        }
//        return ((vein.getBottom().getY() - alienBuilderBotEntity.getBlockPos().getY()) <= 3);
        throw new NotImplementedException();
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if there are resources above and out of reach still
        //return ((vein.getBottom().getY() - alienBuilderBotEntity.getBlockPos().getY()) <= 3);
        throw new NotImplementedException();
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Jump up and place a block below
        throw new NotImplementedException();
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }
}
