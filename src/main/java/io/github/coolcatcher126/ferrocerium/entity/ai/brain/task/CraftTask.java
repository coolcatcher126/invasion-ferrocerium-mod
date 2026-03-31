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
    int i;
    boolean crafted;

    public CraftTask() {
        super(Map.of(
                ModMemoryModuleTypes.CRAFTING, MemoryModuleState.VALUE_PRESENT
                )
        );
    }

    protected boolean shouldRun(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity) {
        return !alienBuilderBotEntity.getItemsToCraft().isEmpty();
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        return !itemsToCraft.isEmpty();
    }

    protected void run(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        itemsToCraft = alienBuilderBotEntity.getItemsToCraft();
        i = itemsToCraft.size() - 1;
        crafted = false;
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (itemsToCraft.isEmpty()) {
            alienBuilderBotEntity.getBrain().forget(ModMemoryModuleTypes.CRAFTING);
        }
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (i >= 0) {
            Map<Item, Integer> itemsRequired;
            if (InvasionFerrocerium.RECIPES.canCraft(itemsToCraft.get(i), alienBuilderBotEntity.getInventory())) {
                itemsRequired = InvasionFerrocerium.RECIPES.getRequiredItemsToCraft(itemsToCraft.get(i));
                for (Map.Entry<Item, Integer> ingredientType : itemsRequired.entrySet()) {
                    int count = ingredientType.getValue();
                    for (int ii = 0; ii < alienBuilderBotEntity.getInventory().size(); ii++) {
                        ItemStack stack = alienBuilderBotEntity.getInventory().getStack(ii);

                        if (stack.getItem() == ingredientType.getKey()) {
                            if (stack.getCount() <= count) {
                                count -= stack.getCount();
                                alienBuilderBotEntity.getInventory().setStack(ii, ItemStack.EMPTY);
                                alienBuilderBotEntity.getInventory().addStack(itemsToCraft.get(i).getDefaultStack());
                                crafted = true;
                            } else {
                                stack.setCount(stack.getCount() - count);
                                count = 0;
                            }
                        }

                        if (count <= 0) {
                            itemsToCraft.remove(i);
                            i--;
                            break;
                        }
                    }
                    if (!crafted) {
                        InvasionFerrocerium.LOGGER.debug("Nothing crafted");
                    }
                }
            }
        }
    }
}
