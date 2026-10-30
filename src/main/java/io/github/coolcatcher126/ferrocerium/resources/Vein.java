package io.github.coolcatcher126.ferrocerium.resources;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.EnumSet;

public class Vein {
    LongArrayList points;
    boolean doNotSave;

    public Vein(boolean doNotSave){
        this.points = new LongArrayList();
        this.doNotSave = doNotSave;
    }

    public Vein(LongArrayList points){
        this.points = points;
        this.doNotSave = false;
    }

    public Vein(){
        this.points = new LongArrayList();
        this.doNotSave = false;
    }

    public BlockPos get(int index){
        return BlockPos.fromLong(this.points.getLong(index));
    }

    public void add(BlockPos e){
        this.points.add(e.asLong());
    }

    public BlockPos remove(int index){
        BlockPos removed = BlockPos.fromLong(this.points.removeLong(index));
        return removed;
    }

    public boolean contains(BlockPos pos){
        return this.points.contains(pos.asLong());
    }

    public int size(){ return this.points.size();}

    public BlockPos getClosest(BlockPos pos) {
        int closestSqrDist = Integer.MAX_VALUE;
        long closestPoint = 0;

        //pos = pos.up();

        for (Long point : this.points) {
            int distX = pos.getX() - BlockPos.unpackLongX(point);
            int distY = pos.getY() - BlockPos.unpackLongY(point);
            int distZ = pos.getZ() - BlockPos.unpackLongZ(point);

            int distSqr = (distX * distX) + (distY * distY) + (distZ * distZ);

            if (closestSqrDist > distSqr){
                closestSqrDist = distSqr;
                closestPoint = point;
            }
        }

        return BlockPos.fromLong(closestPoint);
    }

    public int getClosestIndex(BlockPos pos) {
        int closestSqrDist = Integer.MAX_VALUE;
        long closestPoint = 0;

        for (Long point : this.points) {
            int distX = pos.getX() - BlockPos.unpackLongX(point);
            int distY = pos.getY() - BlockPos.unpackLongY(point);
            int distZ = pos.getZ() - BlockPos.unpackLongZ(point);

            int distSqr = (distX * distX) + (distY * distY) + (distZ * distZ);

            if (closestSqrDist > distSqr){
                closestSqrDist = distSqr;
                closestPoint = point;
            }
        }

        return points.indexOf(closestPoint);
    }

    public void append(Vein other){
        this.points.addAll(other.points);
    }

    public boolean isDoNotSave() {
        return this.doNotSave;
    }

    LongArrayList getPoints() {
        return this.points;
    }

    public static NbtCompound writeToNbt(Vein vein){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("vein", new NbtLongArray(vein.getPoints()));
        return nbtCompound;
    }

    public static Vein readFromNbt(NbtCompound nbtCompound){
        LongArrayList blocks = new LongArrayList();
        NbtLongArray nbtLongArray = new NbtLongArray(nbtCompound.getLongArray("vein"));
        nbtLongArray.forEach(nbtLong -> {blocks.add(nbtLong.longValue());});

        return new Vein(
                blocks
        );
    }
}
