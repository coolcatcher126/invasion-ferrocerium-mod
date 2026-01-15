package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;

public class Vein {
    ArrayList<BlockPos> points;
    ResourceCategory category;
    boolean shouldMineAnyways;

    public Vein(ArrayList<BlockPos> points, ResourceCategory category, boolean shouldMineAnyways){
        this.points = points;
        this.category = category;
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        this.shouldMineAnyways = shouldMineAnyways;
    }

    public Vein(boolean shouldMineAnyways){
        this.points = new ArrayList<>();
        this.category = ResourceCategory.ALL;
        this.shouldMineAnyways = shouldMineAnyways;
    }

    public Vein(ArrayList<BlockPos> points){
        this.points = points;
        this.category = ResourceCategory.ALL;
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
        this.shouldMineAnyways = false;
    }

    public Vein(){
        this.points = new ArrayList<>();
        this.category = ResourceCategory.ALL;
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

    public ResourceCategory getCategory(){
        return this.category;
    }
}
