package io.github.coolcatcher126.ferrocerium.base;

import net.minecraft.data.client.VariantSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BaseSection {
    private BaseSectionTemplate section;
    private  World world;
    private  BlockPos origin;
    private VariantSettings.Rotation rotation;
    boolean isCapitol;

    public BaseSection(
            BaseSectionTemplate sectionTemplate,
            World world,
            BlockPos originPos,
            VariantSettings.Rotation rotation,
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
}
