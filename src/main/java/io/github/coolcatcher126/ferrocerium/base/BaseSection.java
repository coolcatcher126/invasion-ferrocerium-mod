package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.item.Item;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Set;

public class BaseSection {
    private final BaseSectionTemplate section;
    private final World world;
    private final BaseSectPos origin;
    private final BlockRotation rotation;
    private final boolean isCore;
    private ArrayList<BaseBlock> blocks;

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

    /// Returns true when the blocks in the location of the base section matches the base section's template
    public boolean isBuilt(){
        if (blocks == null){
            calculateBaseBlockData();
        }
        for (BaseBlock block : blocks) {
            if (!block.isWantedBlock(world)){
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
}
