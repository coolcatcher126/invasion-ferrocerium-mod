package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Map;

public class CraftTask extends MultiTickTask<AlienBuilderBotEntity> {
    List<Item> itemsToCraft;
    int craftItemIndex;

    public CraftTask() {
        super(Map.of(
                ModMemoryModuleTypes.CRAFTING, MemoryModuleState.VALUE_PRESENT
                )
        );
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        return !(alienBuilderBotEntity.getItemsToCraft().isEmpty() || alienBuilderBotEntity.getInventory().isEmpty());
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        return !(itemsToCraft.isEmpty() || alienBuilderBotEntity.getInventory().isEmpty());
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        itemsToCraft = alienBuilderBotEntity.getItemsToCraft();
        craftItemIndex = itemsToCraft.size() - 1;
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (itemsToCraft.isEmpty()) {
            alienBuilderBotEntity.getBrain().forget(ModMemoryModuleTypes.CRAFTING);
        }
//        else {
//             alienBuilderBotEntity.setItemsToCraft(itemsToCraft);
//        }
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (craftItemIndex >= 0) {
            Map<Item, Integer> itemsRequired;
            if (InvasionFerrocerium.RECIPES.canCraft(itemsToCraft.get(craftItemIndex), alienBuilderBotEntity.getInventory())) {
                itemsRequired = InvasionFerrocerium.RECIPES.getRequiredItemsToCraft(itemsToCraft.get(craftItemIndex));
                for (Map.Entry<Item, Integer> ingredientType : itemsRequired.entrySet()) {
                    int count = ingredientType.getValue();
                    //Look through the inventory for the items needed to craft
                    for (int invSlot = 0; invSlot < alienBuilderBotEntity.getInventory().size(); invSlot++) {
                        ItemStack stack = alienBuilderBotEntity.getInventory().getStack(invSlot);

                        if (stack.getItem() == ingredientType.getKey()) {
                            if (stack.getCount() <= count) {
                                count -= stack.getCount();
                                alienBuilderBotEntity.getInventory().setStack(invSlot, ItemStack.EMPTY);
                            } else {
                                stack.setCount(stack.getCount() - count);
                                count = 0;
                            }
                            if (count == 0) {
                                break;
                            }
                        }
                    }
                }
                //The item has been crafted, put it in the inventory.
                alienBuilderBotEntity.getInventory().addStack(itemsToCraft.remove(craftItemIndex).getDefaultStack());
            }

            //Next item
            craftItemIndex--;
        }
        else {
            //Go back to the first item
            craftItemIndex = itemsToCraft.size() - 1;
        }

    }
}
