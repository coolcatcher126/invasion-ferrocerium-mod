package io.github.coolcatcher126.ferrocerium.components;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import org.ladysnake.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class InvasionLevelComponent implements Component, AutoSyncedComponent {
    /// There are 6 stages:
    /// Stage 0 - No invasion
    /// Stage 1 - Scouts land
    /// Stage 2 - Groups of a few soldiers also spawn in larger scouting parties
    /// Stage 3 - A few small bases are scattered around
    /// Stage 4 - One base is made into the main base, a few small bases
    /// Stage 5 - Full on occupation with all enemies spawning. Large bases everywhere.
    public static final int MAX_INVASION = 5;

    private static final String INVASION_KEY = "invasion_state";

    private int invasionState = 0;

    public int getInvasionState(){
        return this.invasionState;
    }

    public void fightBackInvasion(){
        this.invasionState = MathHelper.clamp(--this.invasionState, 0, MAX_INVASION);
        InvasionFerroceriumComponents.INVASION_LEVEL.sync(this.invasionState);
    }

    public void progressInvasion(){
        this.invasionState = MathHelper.clamp(++this.invasionState, 0, MAX_INVASION);
        InvasionFerroceriumComponents.INVASION_LEVEL.sync(this.invasionState);
    }

    public void setInvasion(int invasionState){
        this.invasionState = MathHelper.clamp(invasionState, 0, MAX_INVASION);
        InvasionFerroceriumComponents.INVASION_LEVEL.sync(this.invasionState);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains(INVASION_KEY, NbtElement.INT_TYPE)) {
            this.invasionState = nbtCompound.getInt(INVASION_KEY);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (this.invasionState > 0) {
            nbtCompound.putInt(INVASION_KEY, this.invasionState);
        }
    }
}
