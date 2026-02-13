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
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class PlaceBaseBlocksTask extends MultiTickTask<AlienBuilderBotEntity> {
    private static final int SEARCH_INTERVAL = 300;
    private static final double MAX_DISTANCE = 1.73;
    private long lastCheckedTime;
    private LinkedList<BaseBlock> blocks;

    public PlaceBaseBlocksTask() {
        super(Map.of(ModMemoryModuleTypes.BASE_SECTION_LOCATION, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        if (serverWorld.getTime() - this.lastCheckedTime < SEARCH_INTERVAL || serverWorld.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheckedTime = serverWorld.getTime();
        Optional<BlockPos> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.BASE_SECTION_LOCATION);
        if (optional.isEmpty()) {
            return false;
        }

        if (alienBuilderBotEntity.getSection().isBuilt()){
            return false;
        }

        BlockPos blockPos = (BlockPos)optional.get();
        return /*alienBuilderBotEntity.getBase().dimension() == serverWorld.getRegistryKey() &&*/ blockPos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if the building is built
        Optional<BlockPos> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.BASE_SECTION_LOCATION);
        if (optional.isEmpty()) {
            return false;
        }

        if (alienBuilderBotEntity.getSection().isBuilt()){
            return false;
        }

        BlockPos blockPos = (BlockPos)optional.get();
        return /*alienBuilderBotEntity.getBase().dimension() == serverWorld.getRegistryKey() &&*/ blockPos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Place down the required blocks one block at a time
        
        BaseBlock block = blocks.peekFirst();
        if (block != null) {
            BlockPos blockPos = alienBuilderBotEntity.getBase().getOrigin().add(block.getBlockPos());

            if (block.isWantedBlock(serverWorld)) {
                return;
            }

            BlockState blockState = block.getBlockState();
            Item blockItem = blockState.getBlock().asItem();
            if (!alienBuilderBotEntity.getInventory().containsAny(x -> x.getItem() == blockItem)) {
                return;
            }

            if (l % 5 != 0) {
                return;
            }

            alienBuilderBotEntity.swingHand(Hand.MAIN_HAND);

            serverWorld.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.FORCE_STATE);
            serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(alienBuilderBotEntity, blockState));
            blocks.remove(block);
            alienBuilderBotEntity.getInventory().removeItem(blockItem, 1);

            throw new NotImplementedException();
        }
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {

    }
}
