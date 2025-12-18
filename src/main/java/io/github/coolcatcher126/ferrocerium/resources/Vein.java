package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Comparator;

public class Vein {
    ArrayList<BlockPos> points;

    public Vein(ArrayList<BlockPos> points){
        this.points = points;
        this.points.sort(Comparator.comparingInt(Vec3i::getY));
    }

    public Vein(){
        this.points = new ArrayList<>();
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
}
