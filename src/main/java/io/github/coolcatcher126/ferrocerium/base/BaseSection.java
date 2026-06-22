package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;

public class BaseSection {
    private AlienBase alienBase;
    private final BaseSectionTemplate section;
    private final World world;
    private final BaseSectPos origin;
    private final BlockRotation rotation;
    private final boolean isCore;
    private ArrayList<BaseBlock> blocks;
    private final ArrayList<BlockPos> chestLocations = new ArrayList<>();

    //Chest search variables
    private final Pair<Integer, Integer> VERTICAL_BOUNDS = new Pair<>(0, 8);
    private final Pair<Integer, Integer> HORIZONTAL_BOUNDS = new Pair<>(-5,5);
    private final int BLOCKS_CHECKED_PER_TICK = 5;
    private int blocksToCheckLeft = BLOCKS_CHECKED_PER_TICK;
    private int x = HORIZONTAL_BOUNDS.getLeft();
    private int y = VERTICAL_BOUNDS.getLeft();
    private int z = HORIZONTAL_BOUNDS.getLeft();

    public BaseSection(
            BaseSectionTemplate sectionTemplate,
            World world,
            BaseSectPos originPos,
            BlockRotation rotation,
            boolean isCore
    ) {
        this.section = sectionTemplate;
        this.world = world;
        this.origin = originPos;
        this.rotation = rotation;
        this.isCore = isCore;
    }

    public void setAlienBase(AlienBase base){
        this.alienBase = base;
    }

    /// Returns true when the blocks in the location of the base section matches the base section's template
    public boolean isBuilt(){
        for (BaseBlock block : getOrCalculateBaseBlockData()) {
            if (!(block.getBlockState().isAir() || block.isWantedBlock(world))){
                return false;
            }
        }
        return true;
    }

    private void calculateBaseBlockData(){
        this.blocks = section.getRelativeBlockData(world);
        for (BaseBlock block : this.blocks) {
            BlockPos pos = block.getBlockPos();
            pos = pos.subtract(new Vec3i(5, 1, 5));//Center the section to the middle
            pos = pos.rotate(rotation);
            pos = pos.add(origin.toBlockPos());
            pos = pos.add(this.alienBase.getOrigin());
            block.setBlockPos(pos);
        }
    }

    public ArrayList<BaseBlock> getOrCalculateBaseBlockData(){
        if (this.blocks == null) {
            calculateBaseBlockData();
        }
        return this.blocks;
    }

    public Set<Item> getBaseBlockPallete(){
        return section.getBlockPallete(world);
    }

    public String getTemplateName(){
        return section.name;
    }

    public BaseSectPos getOrigin(){
        return this.origin;
    }

    public BlockRotation getRotation(){
        return this.rotation;
    }

    public boolean getIsCore(){
        return this.isCore;
    }

    /// Returns a list of all the chests held within the alien base.
    /// <p>Used to allow alien builder bots to deposit and/or pick up collected items.</p>
    public ArrayList<BlockPos> getChestLocations() {
        return chestLocations;
    }

    ///Look through all the blocks within the area of the base section for chests.
    /// <p>probably not very efficient</p>
    public void updateChestLocations() {
        BlockPos sectionOrigin = origin.toBlockPos().add(alienBase.getOrigin());
        BlockPos blockPos;


        while (blocksToCheckLeft > 0) {
            if (x > HORIZONTAL_BOUNDS.getRight()) {
                x = HORIZONTAL_BOUNDS.getLeft();
                z++;
                //continue;
            }
            if (z > HORIZONTAL_BOUNDS.getRight()) {
                z = HORIZONTAL_BOUNDS.getLeft();
                y++;
                //continue;
            }
            if (y > VERTICAL_BOUNDS.getRight()) {
                y = VERTICAL_BOUNDS.getLeft();
                continue;
            }

            blockPos = sectionOrigin.add(x, y, z);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isAir() && blockState.getBlock().equals(Blocks.CHEST)) {
                chestLocations.add(blockPos);
            } else {
                chestLocations.remove(blockPos);
            }
            x++;
            blocksToCheckLeft--;
        }
        blocksToCheckLeft = BLOCKS_CHECKED_PER_TICK;
    }
}
