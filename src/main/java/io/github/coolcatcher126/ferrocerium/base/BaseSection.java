package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.data.client.VariantSettings;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BaseSection {
    private BaseSectionTemplate section;
    private  World world;
    private  BlockPos origin;
    private BlockRotation rotation;
    boolean isCapitol;

    public BaseSection(
            BaseSectionTemplate sectionTemplate,
            World world,
            BlockPos originPos,
            BlockRotation rotation,
            boolean isCapitol
    ) {
        this.section = sectionTemplate;
        this.world = world;
        this.origin = originPos;
        this.rotation = rotation;
        this.isCapitol = isCapitol;
    }

    /// Returns true when the blocks in the location of the base section matches the base section's template
    public boolean isBuilt(){
        //TODO: add functionality
        return  false;
    }

    public ArrayList<BaseBlock> getBaseBlockData(){
        ArrayList<BaseBlock> blocks = section.getRelativeBlockData(world);
        for (BaseBlock block : blocks) {
            BlockPos pos = block.getBlockPos();
            pos.rotate(rotation);
            pos.add(origin);
            block.setBlockPos(pos);
        }
        return blocks;
    }
}
