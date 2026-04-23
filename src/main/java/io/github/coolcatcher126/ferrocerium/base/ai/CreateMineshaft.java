package io.github.coolcatcher126.ferrocerium.base.ai;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;

/**
 * Designates the location of a mineshaft
 *
 * <p>The mineshaft is the series of blocks to be mined and placed.
 *
 * <p>Blocks to be mined will be placed into a {@link io.github.coolcatcher126.ferrocerium.resources.Vein Vein} object in the {@link AlienBase#resources resources} list
 *
 * <p>Grows the mineshaft every few ticks
 */
public class CreateMineshaft implements AlienBaseTask {

    @Override
    public void tick() {

    }
}
