package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Vein {
    ArrayList<BlockPos> points;
    BlockPos top;
    BlockPos bottom;

    public Vein(ArrayList<BlockPos> points){
        this.points = points;
        top = this.points.getFirst();
        bottom = this.points.getFirst();
        calculateBounds();
    }

    public Vein(){
        this.points = new ArrayList<>();
        top = null;
        bottom = null;
    }

    public BlockPos get(int index){
        return this.points.get(index);
    }

    public void add(BlockPos e){
        this.points.add(e);
        calculateBounds();
    }

    public BlockPos remove(int index){
        BlockPos removed = this.points.remove(index);
        calculateBounds();
        return removed;
    }

    public boolean remove(BlockPos blockPos){
        boolean removed = this.points.remove(blockPos);
        calculateBounds();
        return removed;
    }

    public boolean contains(BlockPos pos){
        return this.points.contains(pos);
    }

    public int size(){ return this.points.size();}

    private void calculateBounds(){
        for (BlockPos point : this.points) {
            if (point.getY() > top.getY()){
                top = point;
            }
            if (point.getY() < bottom.getY()){
                bottom = point;
            }
        }
    }

    public BlockPos getFirst() {
        return this.points.getFirst();
    }
}
