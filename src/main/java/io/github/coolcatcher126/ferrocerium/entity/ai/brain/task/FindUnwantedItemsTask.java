package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FindUnwantedItemsTask {
    public static Task<AlienBuilderBotEntity> create(){
        MutableLong timer = new MutableLong(0L);
        return TaskTriggerer.task(
            context -> context.point((world, entity, time) -> {
                if (null == entity.getSection() || entity.getInventory().isEmpty()) {
                    return false;
                } else if (time < timer.getValue()) {
                    return true;
                }
                else {
                    boolean foundUnwanted = false;
                    DefaultedList<ItemStack> held = entity.getInventory().getHeldStacks();
                    List<ItemStack> unwantedItems = entity.getUnwantedItems();
                    Set<Item> itemsToKeep = entity.getWantedItems().stream().map(ItemVariant::getItem).collect(Collectors.toSet());
                    for (ItemStack itemStack : held) {
                        if (!(itemsToKeep.contains(itemStack.getItem()))) {
                            if (!(unwantedItems.contains(itemStack) || itemStack.isEmpty())) {
                                unwantedItems.add(itemStack);
                                foundUnwanted = true;
                            }
                        }
                    }

                    timer.setValue(time + 180L);
                    return foundUnwanted;
                }
            }
        ));
    }
}
