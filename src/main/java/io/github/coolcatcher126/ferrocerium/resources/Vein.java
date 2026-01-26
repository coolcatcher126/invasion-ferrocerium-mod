package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;

public class Vein {
    ArrayList<BlockPos> points;
    EnumSet<ResourceCategory> category;
    boolean shouldMineAnyways;

    public Vein(ArrayList<BlockPos> points, EnumSet<ResourceCategory> category, boolean shouldMineAnyways){
        this.points = points;
        this.category = category;
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        this.shouldMineAnyways = shouldMineAnyways;
    }

    public Vein(boolean shouldMineAnyways){
        this.points = new ArrayList<>();
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.shouldMineAnyways = shouldMineAnyways;
    }

    public Vein(ArrayList<BlockPos> points){
        this.points = points;
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        this.shouldMineAnyways = false;
    }

    public Vein(){
        this.points = new ArrayList<>();
        this.category = EnumSet.allOf(ResourceCategory.class);
        this.shouldMineAnyways = false;
    }

    public BlockPos get(int index){
        return this.points.get(index);
    }

    public void add(BlockPos e){
        this.points.add(e);
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
    }

    public BlockPos remove(int index){
        BlockPos removed = this.points.remove(index);
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        return removed;
    }

    public boolean remove(BlockPos blockPos){
        boolean removed = this.points.remove(blockPos);
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        return removed;
    }

    public boolean contains(BlockPos pos){
        return this.points.contains(pos);
    }

    public int size(){ return this.points.size();}


    public BlockPos getFirst() {
        return this.points.getFirst();
    }
    public BlockPos getBottom() {
        return this.getFirst();
    }
    public BlockPos getLast(){
        return this.points.getLast();
    }
    public BlockPos getTop(){
        return this.getLast();
    }

    public void append(Vein other){
        this.points.addAll(other.points);
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
    }

    public boolean isShouldMineAnyways(){
        return this.shouldMineAnyways;
    }

    public boolean isAboveVein(BlockPos pos){
        return getTop().getY() < pos.getY();
    }

    public boolean isBelowVein(BlockPos pos){
        return getBottom().getY() > pos.getY();
    }

    public EnumSet<ResourceCategory> getCategories(){
        return this.category;
    }

    public static NbtCompound writeToNbt(Vein vein){
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("should_always_mine", vein.isShouldMineAnyways());
        NbtCompound nbtCompound1 = new NbtCompound();
        NbtList nbtList = new NbtList();
        for (ResourceCategory category : vein.getCategories()) {
            nbtCompound1.putString("resource", category.name());
            nbtList.add(nbtCompound1);
        }
        nbtCompound1 = new NbtCompound();
        nbtCompound.put("resource_category", nbtList);
        BlockPos blockPos;
        nbtList = new NbtList();
        while (vein.size() > 0){
            blockPos = vein.remove(0);
            nbtCompound1.putInt("vein_block_z", blockPos.getZ());
            nbtCompound1.putInt("vein_block_y", blockPos.getY());
            nbtCompound1.putInt("vein_block_x", blockPos.getX());
            nbtList.add(nbtCompound1);
        }
        nbtCompound.put("vein", nbtList);
        return nbtCompound;
    }

    public static Vein readfromNbt(NbtCompound nbtCompound){
        ArrayList<BlockPos> blocks = new ArrayList<>();
        NbtList nbtList = nbtCompound.getList("vein", NbtElement.COMPOUND_TYPE);
        for (NbtElement nbtElement : nbtList){
            if (nbtElement instanceof NbtCompound){
                blocks.add(
                        new BlockPos(
                                ((NbtCompound) nbtElement).getInt("vein_block_x"),
                                ((NbtCompound) nbtElement).getInt("vein_block_y"),
                                ((NbtCompound) nbtElement).getInt("vein_block_z")
                        )
                );
            }
            else{
                throw new InvalidNbtException("Vein data does not exist");
            }
        }
        EnumSet<ResourceCategory> resources = EnumSet.noneOf(ResourceCategory.class);
        nbtList = nbtCompound.getList("resource_category", NbtElement.COMPOUND_TYPE);
        String resName;
        for (NbtElement nbtElement : nbtList){
            if (nbtElement instanceof NbtCompound){
                resName = nbtCompound.getString("resource");
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
