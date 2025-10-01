package io.github.coolcatcher126.ferrocerium.components;

import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.NotImplementedException;
import org.ladysnake.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;

public class InvasionLevelComponent implements Component {
    /// There are 6 stages:
    /// Stage 0 - No invasion
    /// Stage 1 - Scouts land
    /// Stage 2 - Groups of a few soldiers also spawn in larger scouting parties
    /// Stage 3 - A few small bases are scattered around
    /// Stage 4 - One base is made into the main base, a few small bases
    /// Stage 5 - Full on occupation with all enemies spawning. Large bases everywhere.
    public static final int MAX_INVASION = 5;

    private static final String INVASION_KEY = "invasion_state";

    //The current invasion state
    private int invasionState = 0;
    // bases that currently exist
    private static final ArrayList<AlienBase> bases = new ArrayList<>();

    public int getInvasionState(){
        return this.invasionState;
    }

    public void fightBackInvasion(){
        this.invasionState = MathHelper.clamp(--this.invasionState, 0, MAX_INVASION);
    }

    public void progressInvasion(){
        this.invasionState = MathHelper.clamp(++this.invasionState, 0, MAX_INVASION);
    }

    public void setInvasion(int invasionState){
        this.invasionState = MathHelper.clamp(invasionState, 0, MAX_INVASION);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains(INVASION_KEY, NbtElement.INT_TYPE)) {
            this.invasionState = nbtCompound.getInt(INVASION_KEY);
        }
        bases = loadBaseListData(nbtCompound);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (this.invasionState > 0) {
            nbtCompound.putInt(INVASION_KEY, this.invasionState);
        }
        saveBaseListData(nbtCompound, bases);
    }

    private static NbtCompound saveBaseListData(NbtCompound nbtCompound, ArrayList<AlienBase> basesToSave){
        nbtCompound.putInt(INVASION_KEY, basesToSave.size());
        for (AlienBase alienBase : basesToSave) {
            saveBaseData(alienBase);
        }
        return nbtCompound;
    }



    private static ArrayList<AlienBase> loadBaseListData(NbtCompound nbtCompound){
        NbtList nbtList = nbtCompound.getList(INVASION_KEY, NbtElement.COMPOUND_TYPE);
        ArrayList<AlienBase> savedBases = new ArrayList<>();
        for (NbtElement nbtElement : nbtList) {
            savedBases.add(loadBaseData(nbtElement));
        }
        return savedBases;
    }

    private static AlienBase loadBaseData(NbtElement nbtElement) {
        nbtElement.
    }

    private static void saveBaseData(AlienBase alienBase) {
        throw new NotImplementedException();
    }
}
