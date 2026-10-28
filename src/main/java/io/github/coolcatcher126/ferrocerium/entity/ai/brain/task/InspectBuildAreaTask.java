package io.github.coolcatcher126.ferrocerium.entity.ai.brain.task;

import io.github.coolcatcher126.ferrocerium.base.BaseBlock;
import io.github.coolcatcher126.ferrocerium.base.BaseBlocks;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class InspectBuildAreaTask {
    private static final double MAX_DISTANCE = 5;

    public static Task<AlienBuilderBotEntity> create() {
        return TaskTriggerer.task(
                context -> context.group(
                    context.queryMemoryValue(ModMemoryModuleTypes.BASE_SECTION_LOCATION),
                    context.queryMemoryValue(ModMemoryModuleTypes.BUILDING),
                    context.queryMemoryOptional(ModMemoryModuleTypes.BUILD_SITE_CLEAR),
                    context.queryMemoryValue(ModMemoryModuleTypes.ACTIVITY_TICKS)
                ).apply(
                        context,
                        (baseSectionLocation,
                         building,
                         buildSiteClear,
                         activityTicks) ->  (serverWorld, alienBuilderBotEntity, time) -> {
                            BlockPos basePos = context.getValue(baseSectionLocation).pos();
                            BaseBlocks blocks;
                            Vein obstructions;

                            if (alienBuilderBotEntity.getToBuild().isBuilt()){
                                activityTicks.remember(0);
                                return false;
                            }

                            boolean withinDistance = alienBuilderBotEntity.getBase().getDimension() == serverWorld.getRegistryKey() && basePos.isWithinDistance(alienBuilderBotEntity.getPos(), MAX_DISTANCE);
                            if (!withinDistance){
                                activityTicks.remember(0);
                                return false;
                            }

                            blocks = context.getValue(building);
                            obstructions = new Vein(true);


                            //Scan the required blocks one block at a time.
                            boolean clear = true;
                            for (BaseBlock block : blocks.getBlocks()) {
                                if (block == null || block.isWantedBlock(serverWorld)) {
                                    continue;
                                }

                                BlockPos blockPos = block.getBlockPos();

                                if (!serverWorld.getBlockState(blockPos).isAir()) {
                                    obstructions.add(blockPos);
                                    clear = false;
                                }
                            }
                            buildSiteClear.remember(Optional.ofNullable(clear ? Unit.INSTANCE : null));
                            if (!clear) {
                                if (obstructions.size() > 0) {
//                                  alienBuilderBotEntity.setVein(obstructions);
                                    alienBuilderBotEntity.getBase().addVeinFirst(obstructions);
//                                  alienBuilderBotEntity.setMining(true);
                                }
                                activityTicks.remember(0);
                            }
                            return true;
                        }
                )
        );
    }
}
