package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Optional;

public class GatherTask extends MultiTickTask<AlienBuilderBotEntity> {
    private static final double MAX_DISTANCE = 5;
    BlockPos resourcePos;
    Vein vein;
    int blockToCollect;

    final int MAX_BREAK_TICKS = 60;//The maximum time in ticks it takes to break a block
    int countTicksToBreak = 0;

    public GatherTask() {
        super(Map.of(ModMemoryModuleTypes.RESOURCE_LOCATION, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        Optional<GlobalPos> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.RESOURCE_LOCATION);
        if (optional.isEmpty()) {
            return false;
        }
        resourcePos = optional.get().pos();

        if (alienBuilderBotEntity.getVein().size() == 0){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && resourcePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if there is a vein to be collected
        if (alienBuilderBotEntity.getVein().size() == 0){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && resourcePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        return withinDistance;
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        vein = alienBuilderBotEntity.getVein();
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Mine the required blocks one block at a time
        breakingInfoTick(serverWorld, alienBuilderBotEntity, l);
        mineBlocks(serverWorld, alienBuilderBotEntity, l);
    }

    /// Sets the block breaking info on the block being mined
    private void breakingInfoTick(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l){
        if (countTicksToBreak >= 0) {
            countTicksToBreak--;
            int progress = Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10);
            serverWorld.setBlockBreakingInfo(alienBuilderBotEntity.getId(), vein.get(blockToCollect), progress);
            alienBuilderBotEntity.swingHand(Hand.MAIN_HAND);
            return;
        }
        countTicksToBreak = MAX_BREAK_TICKS;
    }

    /// Allows the entity to mine blocks. Shared between all mining and gathering goals
    private void mineBlocks(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l){
        if (serverWorld.isAir(vein.get(blockToCollect)) || !(vein.isShouldMineAnyways() || alienBuilderBotEntity.getBase().blockIsCollectible(vein.get(blockToCollect)))){
            vein.remove(blockToCollect);
            return;
        }

        if (countTicksToBreak > 0) {
            countTicksToBreak--;
        } else {
            countTicksToBreak = MAX_BREAK_TICKS;

            //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
            Block blockMined = serverWorld.getBlockState(vein.get(blockToCollect)).getBlock();
            ItemStack blockMinedAsItem = new ItemStack(blockMined.asItem(), 1);
            if (serverWorld.breakBlock(vein.get(blockToCollect), false) && alienBuilderBotEntity.getInventory().canInsert(blockMinedAsItem)){
                serverWorld.emitGameEvent(GameEvent.BLOCK_DESTROY, vein.get(blockToCollect), GameEvent.Emitter.of(alienBuilderBotEntity));
                blockMinedAsItem = alienBuilderBotEntity.addItem(blockMinedAsItem);
                if (!blockMinedAsItem.isEmpty()) {
                    LookTargetUtil.give(alienBuilderBotEntity, blockMinedAsItem, alienBuilderBotEntity.getPos().add(0.0, 1.0, 0.0));
                }

                vein.remove(blockToCollect);
            }
        }
    }
    
    protected boolean isTimeLimitExceeded(long time){
        return false;
    }
}
