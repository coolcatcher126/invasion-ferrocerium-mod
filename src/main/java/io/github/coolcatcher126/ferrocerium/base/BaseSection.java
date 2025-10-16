package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BaseSection {
    private BaseSectionTemplate section;
    private  World world;
    private  BlockPos origin;
    private BlockRotation rotation;
    boolean isCore;

    public BaseSection(
            BaseSectionTemplate sectionTemplate,
            World world,
            BlockPos originPos,
            BlockRotation rotation,
            boolean isCore
    ) {
        this.section = sectionTemplate;
        this.world = world;
        this.origin = originPos;
        this.rotation = rotation;
        this.isCore = isCore;
    }

    /// Returns true when the blocks in the location of the base section matches the base section's template
    public boolean isBuilt(){
        ArrayList<BaseBlock> blocks = this.getBaseBlockData();
        for (BaseBlock block : blocks) {
            if (!block.isPlaced(world)){
                return false;
            }
        }
        return true;
    }

    public ArrayList<BaseBlock> getBaseBlockData(){
        ArrayList<BaseBlock> blocks = section.getRelativeBlockData(world);
        for (BaseBlock block : blocks) {
            BlockPos pos = block.getBlockPos();
            pos = pos.rotate(rotation);
            pos = pos.add(origin);
            block.setBlockPos(pos);
        }
        return blocks;
    }

    public String getTemplateName(){
        return section.name;
    }

    public BlockPos getOrigin(){
        return this.origin;
    }

    public BlockRotation getRotation(){
        return this.rotation;
    }

    public boolean getIsCore(){
        return this.isCore;
    }
}
