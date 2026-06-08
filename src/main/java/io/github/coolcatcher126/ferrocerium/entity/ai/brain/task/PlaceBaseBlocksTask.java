package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.event.GameEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaceBaseBlocksTask extends MultiTickTask<AlienBuilderBotEntity> {
    private static final double MAX_DISTANCE = 5;
    BlockPos basePos;
    private LinkedList<BaseBlock> blocks;
    int blockIndex;
    final int MAX_TICKS_TO_TIMEOUT = 60;
    long timeout = MAX_TICKS_TO_TIMEOUT;

    public PlaceBaseBlocksTask() {
        super(Map.of(ModMemoryModuleTypes.BASE_SECTION_LOCATION, MemoryModuleState.VALUE_PRESENT, ModMemoryModuleTypes.BUILDING, MemoryModuleState.VALUE_PRESENT), 24000);
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

        if (blocks != null && !alienBuilderBotEntity.getInventory().containsAny((blocks.stream().map(block ->
                block.getBlockState().getBlock().asItem())).collect(Collectors.toSet()))) {
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if the building is built or if the task has timed out
        if (timeout <= l || alienBuilderBotEntity.getSection().isBuilt()){
            return false;
        }

        if (!alienBuilderBotEntity.getInventory().containsAny((blocks.stream().map(block ->
            block.getBlockState().getBlock().asItem())).collect(Collectors.toSet()))) {
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        blocks = new LinkedList<>(alienBuilderBotEntity.getSection().getOrCalculateBaseBlockData());
        blockIndex = 0;
        timeout = l + MAX_TICKS_TO_TIMEOUT;
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Place down the required blocks one block at a time

        if (l % 5 != 0) {
            return;
        }

        if (blocks.size() < blockIndex){
            blockIndex = 0;
        }

        BaseBlock block = blocks.get(blockIndex);
        if (block == null) {
            blockIndex++;
            return;
        }

        BlockPos blockPos = block.getBlockPos();

        if (block.isWantedBlock(serverWorld)) {
            blockIndex++;
            return;
        }

        BlockState blockState = block.getBlockState();
        Item blockItem = blockState.getBlock().asItem();
        if (!alienBuilderBotEntity.getInventory().containsAny(x -> x.getItem() == blockItem)) {
            blockIndex++;
            return;
        }

        timeout = l + MAX_TICKS_TO_TIMEOUT;

        alienBuilderBotEntity.swingHand(Hand.MAIN_HAND);

        serverWorld.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
        serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(alienBuilderBotEntity, blockState));
        alienBuilderBotEntity.getInventory().removeItem(blockItem, 1);
        blockIndex++;
    }

//    @Override
//    protected boolean isTimeLimitExceeded(long time) {
//        return false;
//    }

    @Override
    protected void finishRunning(ServerWorld world, AlienBuilderBotEntity entity, long time) {
        entity.getBrain().forget(ModMemoryModuleTypes.BUILDING);
    }
}
