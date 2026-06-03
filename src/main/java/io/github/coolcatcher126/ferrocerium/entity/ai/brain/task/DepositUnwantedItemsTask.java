package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DepositUnwantedItemsTask {
    private static final double MAX_DISTANCE = 5;

    public static Task<AlienBuilderBotEntity> create() {
        return TaskTriggerer.task(
                context -> context.group(
                        context.queryMemoryValue(ModMemoryModuleTypes.BUILDING),
                        context.queryMemoryValue(MemoryModuleType.LOOK_TARGET)
                ).apply(
                        context,
                        (building, lookTarget) ->  (world, entity, time) -> {
                            boolean isBuilding = context.getValue(building);
                            if (!isBuilding || null == entity.getSection() || entity.getInventory().isEmpty()) {
                                return false;
                            } else{
                                BlockPos blockPos = context.getValue(lookTarget).getBlockPos();
                                if (entity.raycast(
                                        MAX_DISTANCE,
                                        0,
                                        false
                                ).getPos().squaredDistanceTo(blockPos.toCenterPos()) > 1){
                                    return false;
                                }
                                else{
                                    BlockState blockState = world.getBlockState(blockPos);
                                    Inventory blockInventory = getBlockInventoryAt(world, blockPos, blockState);
                                    InventoryStorage inventoryStorage = InventoryStorage.of(blockInventory, null);

                                    int chestInventorySlot;

                                    List<ItemStack> unwantedItems = entity.getUnwantedItems();
                                    for (ItemStack unwantedItem : unwantedItems) {
                                        chestInventorySlot = 0;
                                        while (!blockInventory.isValid(chestInventorySlot, unwantedItem)){
                                            chestInventorySlot++;
                                        }
                                        if (unwantedItem.isEmpty()) {
                                            blockInventory.setStack(chestInventorySlot, unwantedItem);
                                            unwantedItem = ItemStack.EMPTY;
                                        } else if (canMergeItems(blockInventory.getStack(chestInventorySlot), unwantedItem)) {
                                            int i = unwantedItem.getMaxCount() - blockInventory.getStack(chestInventorySlot).getCount();
                                            int j = Math.min(unwantedItem.getCount(), i);
                                            unwantedItem.decrement(j);
                                            blockInventory.getStack(chestInventorySlot).increment(j);
                                        }
                                    }

                                    return true;
                                }
                            }
                        }
                )
        );
    }

    @Nullable
    private static Inventory getBlockInventoryAt(World world, BlockPos blockPos, BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            return ((InventoryProvider)block).getInventory(blockState, world, blockPos);
        } else if (blockState.hasBlockEntity() && world.getBlockEntity(blockPos) instanceof Inventory inventory) {
            if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
            }

            return inventory;
        } else {
            return null;
        }
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }
}
