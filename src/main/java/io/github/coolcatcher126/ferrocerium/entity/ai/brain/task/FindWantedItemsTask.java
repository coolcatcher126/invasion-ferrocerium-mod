package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
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

public class FindWantedItemsTask {
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
                    boolean foundWanted = false;
                    List<ItemVariant> wantedItems = entity.getWantedItems();
                    Set<ItemVariant> pallete = entity.getSection().getBaseBlockPallete().stream().map(ItemVariant::of).collect(Collectors.toSet());
                    Set<ItemVariant> itemsToKeep = new java.util.HashSet<>(Set.copyOf(pallete));
                    for (ItemVariant i : pallete) {
                        Map<Item, Integer> map = InvasionFerrocerium.RECIPES.getReqItemsToCraftRec(i.getItem());
                        if (map != null) {
                            Set<ItemVariant> itemSet = map.keySet().stream().map(ItemVariant::of).collect(Collectors.toSet());

                            itemsToKeep.addAll(itemSet);
                        }
                    }
                    wantedItems.addAll(itemsToKeep);

                    timer.setValue(time + 180L);
                    return foundWanted;
                }
            }
        ));
    }
}
