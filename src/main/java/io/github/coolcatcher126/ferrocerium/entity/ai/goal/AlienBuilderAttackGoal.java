package io.github.coolcatcher126.ferrocerium.entity.ai.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class AlienBuilderAttackGoal extends MeleeAttackGoal {
    private final AlienBuilderBotEntity alienBuilderBot;
    private int ticks;

    public AlienBuilderAttackGoal(AlienBuilderBotEntity alienBuilderBot, double speed, boolean pauseWhenMobIdle) {
        super(alienBuilderBot, speed, pauseWhenMobIdle);
        this.alienBuilderBot = alienBuilderBot;
    }

    public void start() {
        super.start();
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.alienBuilderBot.setAttacking(false);
    }

    public void tick() {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.alienBuilderBot.setAttacking(true);
        } else {
            this.alienBuilderBot.setAttacking(false);
        }

    }
}
