package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindUnwantedItemsTask {
    public static Task<AlienBuilderBotEntity> create(){
        MutableLong timer = new MutableLong(0L);
        return TaskTriggerer.task(
                context -> context.group(
                    context.queryMemoryValue(ModMemoryModuleTypes.BUILDING)
          ).apply(
                  context,
                  (building) ->  (world, entity, time) -> {
                      boolean isBuilding = context.getValue(building);
                      if (!isBuilding || null == entity.getSection() || entity.getInventory().isEmpty()) {
                          return false;
                      } else if (time < timer.getValue()) {
                          timer.setValue(time + 60L);
                          return true;}
                      else {
                          boolean foundUnwanted = false;
                          DefaultedList<ItemStack> held = entity.getInventory().getHeldStacks();
                          List<ItemStack> unwantedItems = entity.getUnwantedItems();
                          Set<Item> pallete = entity.getSection().getBaseBlockPallete();
                          Set<Item> itemsToKeep = new java.util.HashSet<>(Set.copyOf(pallete));
                          for (Item i : pallete) {
                              Map<Item, Integer> map = InvasionFerrocerium.RECIPES.getReqItemsToCraftRec(i);
                              if (map != null) {
                                  itemsToKeep.addAll(map.keySet());
                              }
                          }
                          for (ItemStack itemStack : held) {
                              if (!(itemsToKeep.contains(itemStack.getItem()))) {
                                  if (!unwantedItems.contains(itemStack)) {
                                      unwantedItems.add(itemStack);
                                      foundUnwanted = true;
                                  }
                              }
                          }

                          timer.setValue(time + 180L);
                          return foundUnwanted;
                      }
                  }
          )
        );
    }
}
