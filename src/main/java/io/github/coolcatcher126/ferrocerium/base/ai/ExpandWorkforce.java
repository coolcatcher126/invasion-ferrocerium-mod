package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Hires new builders
 *
 * <p>Every few ticks, if there are no idle builders, spawn a builder and add it to the workforce
 */
public class ExpandWorkforce implements AlienBaseTask {
    private final AlienBase alienBase;
    private int baseGrowTime = 12000;

    public ExpandWorkforce(AlienBase alienBase){
        this.alienBase = alienBase;
    }

    @Override
    public void tick() {
        if (baseGrowTime > 0) {
            baseGrowTime--;
        }
        else {
            Optional<AlienBuilderBotEntity> bot = alienBase.getFirstAvailableAlienBuilderBotEntity();
            if (bot.isEmpty()) {
                spawnBuilder();
            }
            baseGrowTime = alienBase.getRandom().nextBetween(3000, 12000);
        }
    }

    /// Adds a newly spawned builder to the builders
    public void spawnBuilder(){
        AlienBuilderBotEntity builder = new AlienBuilderBotEntity(alienBase.getWorld(), this.alienBase);
        builder.refreshPositionAndAngles(Vec3d.of(alienBase.getOrigin()), 0, 0);
        alienBase.hireBuilder(builder);
        alienBase.getWorld().spawnEntity(builder);
    }
}
