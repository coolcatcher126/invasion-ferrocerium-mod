package io.github.coolcatcher126.ferrocerium.entity.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModActivities;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModMemoryModuleTypes;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.task.GatherTask;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.task.PillarTask;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.task.PlaceBaseBlocksTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class AlienBuilderBotBrain {
    protected static Brain<?> create(AlienBuilderBotEntity bot, Brain<AlienBuilderBotEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addBuildActivities(brain);
//        addMineActivities(brain);
//        addChopWoodActivities(brain);
//        addCraftActivities(brain);
        addFightActivities(bot, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                Activity.CORE,
                0,
                ImmutableList.of(
                        new StayAboveWaterTask(1.0F),
                        new LookAroundTask(45, 90),
                        new MoveToTargetTask()
                )
        );
    }

    private static void addIdleActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, UpdateAttackTargetTask.create(AlienBuilderBotBrain::isInvasionStarted, AlienBuilderBotBrain::getPreferredTarget)),
                        Pair.of(1, new RandomTask<>(ImmutableList.of(Pair.of(new WaitTask(20, 100), 1), Pair.of(StrollTask.create(0.6F), 2))))
                )
        );
    }

    private static void addBuildActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                ModActivities.BUILD,
                ImmutableList.of(
                        Pair.of(0, makeGoToBaseSectionTask()),
                        Pair.of(1, new PlaceBaseBlocksTask())
                ),
                ImmutableSet.of(
                        Pair.of(ModMemoryModuleTypes.BASE_SECTION_LOCATION, MemoryModuleState.REGISTERED), Pair.of(ModMemoryModuleTypes.BUILDING, MemoryModuleState.VALUE_PRESENT)
                )
        );
    }

//    private static void addMineActivities(Brain<AlienBuilderBotEntity> brain) {
//        brain.setTaskList(
//                ModActivities.MINE,
//                ImmutableList.of(
//                        Pair.of(0, makeGoToResourceTask()),
//                        Pair.of(1, new GatherTask())
//                ),
//                ImmutableSet.of(
//                        Pair.of(ModMemoryModuleTypes.MINING, MemoryModuleState.VALUE_PRESENT)
//                )
//        );
//    }
//
//    private static void addChopWoodActivities(Brain<AlienBuilderBotEntity> brain) {
//        brain.setTaskList(
//                ModActivities.CHOP_WOOD,
//                ImmutableList.of(
//                        Pair.of(0, makeGoToResourceTask()),
//                        Pair.of(1, new GatherTask()),
//                        Pair.of(2, new PillarTask())
//                ),
//                ImmutableSet.of(
//                        Pair.of(ModMemoryModuleTypes.GATHERING, MemoryModuleState.VALUE_PRESENT)
//                )
//        );
//    }
//
//    private static void addCraftActivities(Brain<AlienBuilderBotEntity> brain) {
//        brain.setTaskList(
//                ModActivities.CRAFT,
//                ImmutableList.of(
//
//                )
//        );
//    }

    private static void addFightActivities(AlienBuilderBotEntity bot, Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                Activity.FIGHT,
                0,
                ImmutableList.of(
                        ForgetAttackTargetTask.create(target -> !Sensor.testAttackableTargetPredicate(bot, target) || !isInvasionStarted(bot)),
                        RangedApproachTask.create(1.0F),
                        MeleeAttackTask.create(10)
                ),
                MemoryModuleType.ATTACK_TARGET
        );
    }

    public static void updateActivities(AlienBuilderBotEntity bot) {
        Brain<AlienBuilderBotEntity> brain = bot.getBrain();
        brain.resetPossibleActivities(ImmutableList.of(ModActivities.BUILD, /*ModActivities.CRAFT, ModActivities.MINE, ModActivities.CHOP_WOOD,*/ Activity.FIGHT, Activity.IDLE));
    }

    private static boolean isInvasionStarted(AlienBuilderBotEntity bot) {
        return (InvasionFerroceriumComponents.getInvasionLevel(bot.getEntityWorld()) > 0);
    }

    private static Optional<? extends LivingEntity> getPreferredTarget(AlienBuilderBotEntity bot){
        Brain<AlienBuilderBotEntity> brain = bot.getBrain();
        Optional<LivingEntity> optional = brain.getOptionalRegisteredMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (optional.isPresent()) {
            return optional;
        }

        Optional<PlayerEntity> optional2 = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (optional2.isPresent()) {
            return optional2;
        }

        Optional<LivingTargetCache> optional3 = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
        if (optional3.isPresent()) {
            return optional3.get().findFirst((e) -> !(e instanceof InvasionBotEntity));
        }

        return Optional.empty();
    }

    private static Task<PathAwareEntity> makeGoToBaseSectionTask() {
        return GoToRememberedPositionTask.createPosBased(ModMemoryModuleTypes.BASE_SECTION_LOCATION, 1.0F, 8, false);
    }

    private static Task<PathAwareEntity> makeGoToResourceTask() {
        return GoToRememberedPositionTask.createPosBased(ModMemoryModuleTypes.RESOURCE_LOCATION, 1.0F, 8, false);
    }
}
