package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

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
        super(Map.of(
                ModMemoryModuleTypes.RESOURCE_LOCATION, MemoryModuleState.VALUE_PRESENT,
                        MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED),
                24000);
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        Optional<GlobalPos> optional = alienBuilderBotEntity.getBrain().getOptionalRegisteredMemory(ModMemoryModuleTypes.RESOURCE_LOCATION);
        if (optional.isEmpty()) {
            return false;
        }
        resourcePos = optional.get().pos();

        if (null == alienBuilderBotEntity.getVein() || alienBuilderBotEntity.getVein().size() == 0){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey();
        withinDistance = withinDistance && resourcePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
//        withinDistance = withinDistance && alienBuilderBotEntity.raycast(
//                MAX_DISTANCE,
//                0,
//                false
//        ).getPos().squaredDistanceTo(resourcePos.toCenterPos()) < 1;
        return withinDistance;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Check to see if there is a vein to be collected or the task has timed out
        if (null == alienBuilderBotEntity.getVein() || alienBuilderBotEntity.getVein().size() == 0){
            return false;
        }

        boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey();
        withinDistance = withinDistance  && resourcePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
        //withinDistance = withinDistance && serverWorld.raycast(new RaycastContext(alienBuilderBotEntity.getPos(), resourcePos.toCenterPos(), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, alienBuilderBotEntity)).getBlockPos().equals(resourcePos);
        return withinDistance;
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        vein = alienBuilderBotEntity.getVein();
        blockToCollect = vein.getClosestIndex(alienBuilderBotEntity.getBlockPos());
        countTicksToBreak = MAX_BREAK_TICKS;
        alienBuilderBotEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(resourcePos));
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        //Mine the required blocks one block at a time
        blockToCollect = vein.getClosestIndex(alienBuilderBotEntity.getBlockPos());

        resourcePos = vein.get(blockToCollect);
        while (serverWorld.isAir(resourcePos) || !(vein.isShouldMineAnyways() || InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(serverWorld, resourcePos))) {
            vein.remove(blockToCollect);
            blockToCollect = vein.getClosestIndex(alienBuilderBotEntity.getBlockPos());
            resourcePos = vein.get(blockToCollect);
            alienBuilderBotEntity.setVein(vein);
        }

        alienBuilderBotEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(resourcePos));

        if (alienBuilderBotEntity.raycast(
                MAX_DISTANCE,
                0,
                false
        ).getPos().squaredDistanceTo(resourcePos.toCenterPos()) < 1) {
            breakingInfoTick(serverWorld, alienBuilderBotEntity, l);
            mineBlocks(serverWorld, alienBuilderBotEntity, l);
        }
    }

    /// Sets the block breaking info on the block being mined
    private void breakingInfoTick(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l){
        if (countTicksToBreak >= 0) {
            int progress = Math.round((MAX_BREAK_TICKS - countTicksToBreak) / (float) MAX_BREAK_TICKS * 10);
            serverWorld.setBlockBreakingInfo(alienBuilderBotEntity.getId(), vein.get(blockToCollect), progress);
            alienBuilderBotEntity.swingHand(Hand.MAIN_HAND);
        }
    }

    /// Allows the entity to mine blocks. Shared between all mining and gathering goals
    private void mineBlocks(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l){
        if (countTicksToBreak > 0) {
            countTicksToBreak--;
        } else {
            countTicksToBreak = MAX_BREAK_TICKS;

            //Make the bot  mine it as well as all/many other adjacent blocks of the same type.
            Block blockMined = serverWorld.getBlockState(vein.get(blockToCollect)).getBlock();
            ItemStack blockMinedAsItem = new ItemStack(blockMined.asItem(), 1);
            if (serverWorld.breakBlock(vein.get(blockToCollect), false) && alienBuilderBotEntity.getInventory().canInsert(blockMinedAsItem)){
                blockMinedAsItem = alienBuilderBotEntity.addItem(blockMinedAsItem);
                if (!blockMinedAsItem.isEmpty()) {
                    LookTargetUtil.give(alienBuilderBotEntity, blockMinedAsItem, alienBuilderBotEntity.getPos().add(0.0, 1.0, 0.0));
                }

                vein.remove(blockToCollect);
                alienBuilderBotEntity.setVein(vein);
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld world, AlienBuilderBotEntity entity, long time) {
        if (vein != null && vein.size() > 0) {
            entity.getBase().addVeinFirst(vein);
        }
        entity.getBrain().resetPossibleActivities();
    }
}
