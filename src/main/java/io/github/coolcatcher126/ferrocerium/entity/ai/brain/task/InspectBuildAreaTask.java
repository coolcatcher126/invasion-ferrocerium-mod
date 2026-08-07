package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InspectBuildAreaTask extends MultiTickTask<AlienBuilderBotEntity> {
    private static final double MAX_DISTANCE = 5;
    BlockPos basePos;
    private List<BaseBlock> blocks;

    Vein obstructions;

    public InspectBuildAreaTask() {
        super(Map.of(ModMemoryModuleTypes.BASE_SECTION_LOCATION, MemoryModuleState.VALUE_PRESENT,
                ModMemoryModuleTypes.BUILDING, MemoryModuleState.VALUE_PRESENT,
                ModMemoryModuleTypes.BUILD_SITE_CLEAR, MemoryModuleState.REGISTERED,
                ModMemoryModuleTypes.ACTIVITY_TICKS, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        Optional<GlobalPos> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.BASE_SECTION_LOCATION);
        if (optional.isEmpty()) {
            return false;
        }
        basePos = optional.get().pos();

        if (alienBuilderBotEntity.getSection().isBuilt()){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        Optional<Integer> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.ACTIVITY_TICKS);
        //Check to see if the building is built.
        if (optional.isEmpty() || alienBuilderBotEntity.getSection().isBuilt()){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        blocks = alienBuilderBotEntity.getBrain().getOptionalMemory(ModMemoryModuleTypes.BUILDING).get();
        obstructions = new Vein(true);
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (l % 5 != 0) {
            return;
        }

        //Scan the required blocks one block at a time.
        boolean clear = true;
        for (BaseBlock block : blocks) {
            if (block == null) {
                continue;
            }

            if (block.isWantedBlock(serverWorld)) {
                continue;
            }

            BlockPos blockPos = block.getBlockPos();

            if (!serverWorld.getBlockState(blockPos).isAir()) {
                obstructions.add(blockPos);
                clear = false;
            }
        }
        alienBuilderBotEntity.getBrain().remember(ModMemoryModuleTypes.BUILD_SITE_CLEAR, clear ? Unit.INSTANCE : null);
    }

    @Override
    protected boolean isTimeLimitExceeded(long time) {
        return false;
    }

    @Override
    protected void finishRunning(ServerWorld world, AlienBuilderBotEntity entity, long time) {
        if (obstructions.size() > 0) {
            entity.setVein(obstructions);
            entity.getBase().addVeinFirst(obstructions);
            entity.setMining(true);
        }
    }
}
