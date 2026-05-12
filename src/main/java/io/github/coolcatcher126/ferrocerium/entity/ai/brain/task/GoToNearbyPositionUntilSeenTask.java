package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.RaycastContext;
import org.apache.commons.lang3.mutable.MutableLong;

public class GoToNearbyPositionUntilSeenTask {
    public static Task<PathAwareEntity> create(MemoryModuleType<GlobalPos> posModule, float walkSpeed, int completionRange, int maxDistance, double reach) {
        MutableLong mutableLong = new MutableLong(0L);
        return TaskTriggerer.task(
                context -> context.group(context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(posModule))
                        .apply(context, (walkTarget, pos) -> (world, entity, time) -> {
                            GlobalPos globalPos = context.getValue(pos);
                            if (
                                world.getRegistryKey() != globalPos.dimension() ||
                                !globalPos.pos().isWithinDistance(entity.getPos(), maxDistance) ||
                                entity.raycast(
                                        reach,
                                        0,
                            false
                                ).getPos().squaredDistanceTo(globalPos.pos().toCenterPos()) < 1
                            ) {
                                return false;
                            } else if (time <= mutableLong.getValue()) {
                                return true;
                            } else {
                                walkTarget.remember(new WalkTarget(globalPos.pos(), walkSpeed, completionRange));
                                mutableLong.setValue(time + 80L);
                                return true;
                            }
                        })
        );
    }
}
