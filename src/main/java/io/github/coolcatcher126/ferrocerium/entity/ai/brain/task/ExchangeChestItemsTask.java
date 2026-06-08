package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableLong;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExchangeChestItemsTask {
    private static final double MAX_DISTANCE = 5;

    public static Task<AlienBuilderBotEntity> create() {
        MutableLong timer = new MutableLong(0L);
        return TaskTriggerer.task(
                context -> context.group(
                        context.queryMemoryValue(ModMemoryModuleTypes.CHEST_LOCATION)
                ).apply(
                        context,
                        (chestLocation) ->  (world, entity, time) -> {
                            if (null == entity.getSection() || (entity.getWantedItems().isEmpty() && entity.getUnwantedItems().isEmpty())) {
                                return false;
                            }
                            else if (time < timer.getValue()) {
                                return true;
                            }
                            else {
                                BlockPos blockPos = context.getValue(chestLocation).pos();
                                entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
                                if (entity.raycast(
                                        MAX_DISTANCE,
                                        0,
                                        false
                                ).getPos().squaredDistanceTo(blockPos.toCenterPos()) > 1) {
                                    return true;
                                } else {
                                    BlockState blockState = world.getBlockState(blockPos);
                                    Inventory blockInventory = getBlockInventoryAt(world, blockPos, blockState);
                                    InventoryStorage inventoryStorage = InventoryStorage.of(blockInventory, null);
                                    InventoryStorage entityInv = entity.inventoryWrapper;

                                    int failedOrZeroTransactions = 0;
                                    int totalTransactions = 0;

                                    List<ItemVariant> wantedItems = entity.getWantedItems();
                                    for (ItemVariant wantedItem : wantedItems) {
                                        try (Transaction transaction = Transaction.openOuter()){
                                            long stackMoved = inventoryStorage.extract(wantedItem, (64*9), transaction);
                                            entityInv.insert(wantedItem, stackMoved, transaction);
                                            transaction.commit();
                                            if (stackMoved == 0){
                                                failedOrZeroTransactions++;
                                            }
                                        } catch (Exception e) {
                                            failedOrZeroTransactions++;
                                        }
                                        finally {
                                            totalTransactions++;
                                        }
                                    }

                                    List<ItemStack> unwantedItems = entity.getUnwantedItems();
                                    for (int i = unwantedItems.size() - 1; i >= 0; i--) {
                                        ItemStack unwantedItem = unwantedItems.get(i);
                                        try (Transaction transaction = Transaction.openOuter()) {
                                            ItemVariant itemVariant = ItemVariant.of(unwantedItem);
                                            long stackMoved = entityInv.extract(itemVariant, unwantedItem.getCount(), transaction);
                                            inventoryStorage.insert(itemVariant, stackMoved, transaction);
                                            transaction.commit();
                                            unwantedItems.remove(i);
                                            if (stackMoved == 0){
                                                failedOrZeroTransactions++;
                                            }
                                        }
                                        catch (Exception e){
                                            failedOrZeroTransactions++;
                                        }
                                        finally {
                                            totalTransactions++;
                                        }
                                    }
                                    timer.setValue(time + 60L);
                                    return totalTransactions > failedOrZeroTransactions;
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
