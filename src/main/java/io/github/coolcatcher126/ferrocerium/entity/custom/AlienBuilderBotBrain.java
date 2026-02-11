package io.github.coolcatcher126.ferrocerium.entity.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.ai.brain.ModActivities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.Optional;

public class AlienBuilderBotBrain {
    protected static Brain<?> create(Brain<AlienBuilderBotEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addBuildActivities(brain);
        addMineActivities(brain);
        addChopWoodActivities(brain);
        addFightActivities(brain);
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
                        new LookAroundTask(45, 90), new MoveToTargetTask()
                )
        );
    }

    private static void addIdleActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, UpdateAttackTargetTask.create(AlienBuilderBotBrain::getPreferredTarget)),
                        Pair.of(1, new RandomTask<>(ImmutableList.of(Pair.of(new WaitTask(20, 100), 1), Pair.of(StrollTask.create(0.6F), 2))))
                )
        );
    }

    private static void addBuildActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                ModActivities.BUILD,
                ImmutableList.of(

                )
        );
    }

    private static void addMineActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                ModActivities.MINE,
                ImmutableList.of(

                )
        );
    }

    private static void addChopWoodActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                ModActivities.CHOP_WOOD,
                ImmutableList.of(

                )
        );
    }

    private static void addFightActivities(Brain<AlienBuilderBotEntity> brain) {
        brain.setTaskList(
                Activity.FIGHT,
                0,
                ImmutableList.of(

                ),
                MemoryModuleType.ATTACK_TARGET
        );
    }

    public static void updateActivities(AlienBuilderBotEntity bot) {

    }

    private static Optional<? extends LivingEntity> getPreferredTarget(AlienBuilderBotEntity bot){
        Optional<LivingEntity> optional = bot.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        return optional;
    }
}
