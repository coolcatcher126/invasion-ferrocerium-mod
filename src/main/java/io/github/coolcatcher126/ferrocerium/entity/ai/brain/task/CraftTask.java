package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftTask extends MultiTickTask<AlienBuilderBotEntity> {
    List<Item> itemsToCraft;

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
    }

    protected void finishRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        alienBuilderBotEntity.getBrain().forget(ModMemoryModuleTypes.CRAFTING);
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        Map<Item, Integer> itemsRequired;
        for (Item item : itemsToCraft) {
            if (InvasionFerrocerium.RECIPES.canCraft(item, alienBuilderBotEntity.getInventory())) {
                itemsRequired = InvasionFerrocerium.RECIPES.getRequiredItemsToCraft(item);
                for (Map.Entry<Item, Integer> ingredientType : itemsRequired.entrySet()) {
                    int count = ingredientType.getValue();
                    for (int i = 0; i < alienBuilderBotEntity.getInventory().size(); i++) {
                        ItemStack stack = alienBuilderBotEntity.getInventory().getStack(i);

                        if (stack.getItem() == ingredientType.getKey()) {
                            if (stack.getCount() <= count) {
                                count -= stack.getCount();
                                alienBuilderBotEntity.getInventory().setStack(i, ItemStack.EMPTY);
                                alienBuilderBotEntity.getInventory().addStack(item.getDefaultStack());
                            } else {
                                stack.setCount(stack.getCount() - count);
                                count = 0;
                            }
                        }

                        if (count <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
