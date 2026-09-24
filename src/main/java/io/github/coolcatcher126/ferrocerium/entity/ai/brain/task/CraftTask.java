package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.BuilderBotConversions;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftTask extends MultiTickTask<AlienBuilderBotEntity> {
    List<ItemVariant> itemsToCraft;
    int craftItemIndex;

    public CraftTask() {
        super(Map.of());
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
            alienBuilderBotEntity.setItemsToCraft(new ArrayList<>());
        }
        else {
             alienBuilderBotEntity.setItemsToCraft(itemsToCraft);
        }
    }

    protected void keepRunning(ServerWorld serverWorld, AlienBuilderBotEntity alienBuilderBotEntity, long l) {
        if (craftItemIndex >= 0) {
            InvasionFerrocerium.RECIPES.tryCraftItem(itemsToCraft.remove(craftItemIndex), alienBuilderBotEntity.inventoryWrapper);

            //Next item
            craftItemIndex--;
        }
        else {
            //Go back to the first item
            craftItemIndex = itemsToCraft.size() - 1;
        }

    }
}
