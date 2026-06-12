package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;

public class WorkTimerTask {
    public static Task<LivingEntity> create(MemoryModuleType<Integer> memory) {
        return TaskTriggerer.task(
                context -> context.group(context.queryMemoryValue(memory))
                        .apply(context, (timer) -> (world, entity, time) -> {
                            int i = context.<Integer>getValue(timer);
                            if (i <= 0) {
                                timer.forget();
                                entity.getBrain().resetPossibleActivities();
                            } else {
                                timer.remember(i - 1);
                            }

                            return true;
                        })
        );
    }
}
