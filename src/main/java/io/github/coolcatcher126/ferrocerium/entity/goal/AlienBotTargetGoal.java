package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AlienBotTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public AlienBotTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    public AlienBotTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, checkVisibility, targetPredicate);
    }

    public AlienBotTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        super(mob, targetClass, checkVisibility, checkCanNavigate);
    }

    public AlienBotTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    public boolean canStart() {
        boolean invasionStart = InvasionFerroceriumComponents.getInvasionLevel(super.mob.getEntityWorld()) > 0;
        return invasionStart && super.canStart();
    }
}