package io.github.coolcatcher126.ferrocerium.resources;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.EnumSet;

public class Vein {
    LongArrayList points;
    EnumSet<ResourceCategory> category;
    boolean shouldMineAnyways;
    boolean doNotSave;

    public Vein(LongArrayList points, EnumSet<ResourceCategory> category, boolean shouldMineAnyways){
        this.points = points;
        this.category = category;
        this.shouldMineAnyways = shouldMineAnyways;
        this.doNotSave = false;
    }

    public Vein(boolean shouldMineAnyways, boolean doNotSave){
        this.points = new LongArrayList();
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.shouldMineAnyways = shouldMineAnyways;
        this.doNotSave = doNotSave;
    }

    public Vein(LongArrayList points){
        this.points = points;
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.shouldMineAnyways = false;
        this.doNotSave = false;
    }

    public Vein(){
        this.points = new LongArrayList();
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.shouldMineAnyways = false;
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

    public boolean isShouldMineAnyways(){
        return this.shouldMineAnyways;
    }

    public boolean isDoNotSave() {
        return this.doNotSave;
    }

    public EnumSet<ResourceCategory> getCategories(){
        return this.category;
    }

    LongArrayList getPoints() {
        return this.points;
    }

    public static NbtCompound writeToNbt(Vein vein){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("should_always_mine", vein.isShouldMineAnyways());
        NbtCompound nbtCompound1;
        NbtList nbtList = new NbtList();
        for (ResourceCategory category : vein.getCategories()) {
            nbtCompound1 = new NbtCompound();
            nbtCompound1.putString("resource", category.name());
            nbtList.add(nbtCompound1);
        }
        nbtCompound.put("resource_category", nbtList);

        nbtCompound.put("vein", new NbtLongArray(vein.getPoints()));
        return nbtCompound;
    }

    public static Vein readFromNbt(NbtCompound nbtCompound){
        LongArrayList blocks = new LongArrayList();
        NbtLongArray nbtLongArray = new NbtLongArray(nbtCompound.getLongArray("vein"));
        nbtLongArray.forEach(nbtLong -> {blocks.add(nbtLong.longValue());});
        EnumSet<ResourceCategory> resources = EnumSet.noneOf(ResourceCategory.class);
        NbtList nbtList = nbtCompound.getList("resource_category", NbtElement.COMPOUND_TYPE);
        String resName;
        for (NbtElement nbtElement : nbtList){
            if (nbtElement instanceof NbtCompound){
                resName = ((NbtCompound) nbtElement).getString("resource");
                resources.add(ResourceCategory.valueOf(resName));
            }
            else{
                throw new InvalidNbtException("Resource category data does not exist");
            }
        }
        return new Vein(
                blocks,
                resources,
                nbtCompound.getBoolean("should_always_mine")
        );
    }
}
