package io.github.coolcatcher126.ferrocerium.base.ai;


import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.base.AlienBase;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Designates the location of resources such as wood and trees
 *
 * <p>Blocks to be mined will be placed into a {@link io.github.coolcatcher126.ferrocerium.resources.Vein Vein} object using {@link AlienBase#addVein(Vein)}}
 *
 * <p>Scans the environment every few ticks
 */
public class ScanForResources implements AlienBaseTask  {
    private int minBlockSearchRadius = 0;//The minimum distance to search for blocks to collect (centered on the base)
    private int maxBlockSearchRadius = 30;//The maximum distance to search for blocks to collect (centered on the base)

    private final AlienBase alienBase;
    private final int SEARCH_TIME = 1200;
    private int search_time_count = SEARCH_TIME;

    public ScanForResources(AlienBase alienBase){
        this.alienBase = alienBase;
    }

    @Override
    public void tick() {
        //Don't look for things to mine all the time
        if (search_time_count > 0){
            search_time_count--;
        }
        else{
            findResourcesToCollect();
            search_time_count = SEARCH_TIME;
        }
    }

    public void setBlockSearchRadius(int minBlockSearchRadius, int maxBlockSearchRadius) {
        this.minBlockSearchRadius = minBlockSearchRadius;
        this.maxBlockSearchRadius = maxBlockSearchRadius;
    }

    /// Searches in the area between the min and max search radii to find resources to collect.
    /// <p>The base uses the resources found to send builder bots to investigate and mine</p>
    /// <p>Resources being: Wood, ores, stone</p>
    public void findResourcesToCollect(){
        final AtomicReference<BlockPos> searchedBlock = new AtomicReference<>();
        for (int x = this.minBlockSearchRadius; x <= this.maxBlockSearchRadius; x++) {
            for (int y = -5; y <= 5; y++){
                for (int z = this.minBlockSearchRadius; z <= this.maxBlockSearchRadius; z++) {
                    //+x+z
                    searchedBlock.set(alienBase.getOrigin().add(x, y, z));
                    if (alienBase.getResources().stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        alienBase.addVein(new Vein(vein));
                    }
                    //-x+z
                    searchedBlock.set(alienBase.getOrigin().add(-x, y, z));
                    if (alienBase.getResources().stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        alienBase.addVein(new Vein(vein));
                    }
                    //+x-z
                    searchedBlock.set(alienBase.getOrigin().add(x, y, -z));
                    if (alienBase.getResources().stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        alienBase.addVein(new Vein(vein));
                    }
                    //-x-z
                    searchedBlock.set(alienBase.getOrigin().add(-x, y, -z));
                    if (alienBase.getResources().stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        alienBase.addVein(new Vein(vein));
                    }
                }
            }
        }
    }

    /// Searches in the area around the given block pos recursively to find resources to collect.
    void findAdjacentResourcesToCollect(BlockPos blockPos, List<BlockPos> resources){
        BlockPos searchedBlock;
        //Check +x
        searchedBlock = blockPos.add(1, 0, 0);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -x
        searchedBlock = blockPos.add(-1, 0, 0);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check +y
        searchedBlock = blockPos.add(0, 1, 0);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -y
        searchedBlock = blockPos.add(0, -1, 0);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check +z
        searchedBlock = blockPos.add(0, 0, 1);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -z
        searchedBlock = blockPos.add(0, 0, -1);
        if (!resources.contains(searchedBlock) && InvasionFerrocerium.COLLECTIBLE_RESOURCES.blockIsCollectible(alienBase.getWorld(), searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
    }
}
