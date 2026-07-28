package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
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

import java.util.*;
import java.util.stream.Collectors;

public class PlaceBaseBlocksTask extends MultiTickTask<AlienBuilderBotEntity> {
    private static final double MAX_DISTANCE = 5;
    BlockPos basePos;
    private List<BaseBlock> blocks;
    int blockIndex;
    final int MAX_TICKS_TO_TIMEOUT = 60;
    long timeout = MAX_TICKS_TO_TIMEOUT;

    Vein obstructions;

    boolean willExchange = false;

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
            alienBuilderBotEntity.getBrain().resetPossibleActivities();
            alienBuilderBotEntity.setExchanging(true);
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
            willExchange = true;
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        blocks = alienBuilderBotEntity.getBrain().getOptionalMemory(ModMemoryModuleTypes.BUILDING).get();
        blockIndex = 0;
        timeout = l + MAX_TICKS_TO_TIMEOUT;
        willExchange = false;
        obstructions = new Vein(true);
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Place down the required blocks one block at a time

        BaseBlock block;
        BlockPos blockPos;
        do {
            if (blocks.size() < blockIndex){
                blockIndex = 0;
            }

            block = blocks.get(blockIndex);
            if (block == null) {
                blockIndex++;
                continue;
            }

            if (block.isWantedBlock(serverWorld)) {
                blockIndex++;
                continue;
            }

            blockPos = block.getBlockPos();

            if (!serverWorld.getBlockState(blockPos).isAir()) {
                obstructions.add(blockPos);
                blockIndex++;
                continue;
            }
            break;
        }
        while (true);

        if (l % 5 != 0) {
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

    @Override
    protected void finishRunning(ServerWorld world, AlienBuilderBotEntity entity, long time) {
        if (obstructions.size() > 0) {
            entity.setVein(obstructions);
            entity.setMining(true);
        }
        entity.getBrain().resetPossibleActivities();
        if (willExchange) {
            entity.setExchanging(true);
        }
    }
}
