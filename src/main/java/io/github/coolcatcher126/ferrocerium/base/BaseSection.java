package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
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
    public ArrayList<BlockPos> getChestLocations(){
        chestLocations.clear();
        if (this.blocks == null) {
            calculateBaseBlockData();
        }
        for (BaseBlock block : blocks) {
            BlockState blockState = world.getBlockState(block.getBlockPos());
            if (!blockState.isAir() && blockState.getBlock().equals(Blocks.CHEST)){
                chestLocations.add(block.getBlockPos());
            }
        }
        return chestLocations;
    }
}
