package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;

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
                          for (int slot = 0; slot < held.size(); slot++) {
                              if (!entity.getSection().getBaseBlockPallete().contains(held.get(slot).getItem())) {
                                  if (!unwantedItems.contains(held.get(slot))) {
                                      unwantedItems.add(held.get(slot));
                                      foundUnwanted = true;
                                  }
                              }
                          }

                          timer.setValue(time + 60L);
                          return foundUnwanted;
                      }
                  }
          )
        );
    }
}
