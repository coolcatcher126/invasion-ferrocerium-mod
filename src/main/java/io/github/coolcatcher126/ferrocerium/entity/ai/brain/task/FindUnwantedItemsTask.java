package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class FindUnwantedItemsTask {
    public static Task<AlienBuilderBotEntity> create(){
        return TaskTriggerer.task(
                context -> context.group(
                    context.queryMemoryValue(ModMemoryModuleTypes.BUILDING)
          ).apply(
                  context,
                  (building) ->  (world, entity, time) -> {
                      boolean isBuilding = context.getValue(building);
                      if (isBuilding && null != entity.getSection() && !entity.getInventory().isEmpty()){
                          DefaultedList<ItemStack> held = entity.getInventory().getHeldStacks();
                          for (int i = 0; i < held.size(); i++) {
                              if (!entity.getSection().getBaseBlockPallete().contains(held.get(i).getItem())) {
                                  entity.giveAway(i);
                              }
                          }
                          return true;
                      }
                      else {
                          return false;
                      }
                  }
          )
        );
    }
}
