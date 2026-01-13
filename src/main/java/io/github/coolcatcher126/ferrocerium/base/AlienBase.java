package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import io.github.coolcatcher126.ferrocerium.resources.ResourceCategory;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/// A single base.
/// Each base owns one or more Alien Builder Bots.
public class AlienBase {
    private int minBlockSearchRadius = 0;//The minimum distance to search for blocks to collect (centered on the base)
    private int maxBlockSearchRadius = 30;//The maximum distance to search for blocks to collect (centered on the base)

    BlockPos origin;
    ArrayList<BaseSection> sections;
    ArrayList<AlienBuilderBotEntity> builders;
    ArrayList<BaseSectPos> availablePos;

    ArrayList<BaseSectionTemplate> sectionTemplateList;

    List<Block> COLLECTIBLE_WOOD_BLOCKS = List.of(
            Blocks.ACACIA_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.CHERRY_LOG, Blocks.OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG,
            Blocks.ACACIA_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.CHERRY_PLANKS, Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.JUNGLE_PLANKS,
            Blocks.ACACIA_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.CHERRY_LEAVES, Blocks.OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES
            );

    List<Block> COLLECTIBLE_ORE_BLOCKS = List.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, ModBlocks.ALUMINUM_ORE_BLOCK, ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK
            );

    List<Block> COLLECTIBLE_BLOCKS = Stream.concat(COLLECTIBLE_WOOD_BLOCKS.stream(), Stream.concat(Stream.of(Blocks.STONE), COLLECTIBLE_ORE_BLOCKS.stream())).toList();

    List<Integer> STRIP_MINE_LEVELS = List.of(-53, -52, 16, 17, 48, 49);

    World world;

    private int baseGrowTime;

    private final int SEARCH_TIME = 20;
    private int search_time_count = SEARCH_TIME;

    ArrayList<Vein> resources = new ArrayList<>();//Things to mine
    ArrayList<BaseBlock> baseBlocks = new ArrayList<>();//Things to build that are not base sections

    protected final Random random = Random.create();
    UUID uuid = MathHelper.randomUuid(this.random);

    public AlienBase(World world, BlockPos origin, ArrayList<BaseSection> sections, ArrayList<AlienBuilderBotEntity> builders, UUID uuid){
        this.world = world;
        this.origin = origin;

        this.sections = sections;
        this.builders = builders;
        this.uuid = uuid;


        sectionTemplateList = new ArrayList<>();
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);
        this.availablePos = new ArrayList<>();
        baseSectGetAvailablePos();

        createMineshaft();
        mineResourceVein(resources.get(0));
    }

    public AlienBase(World world, BlockPos origin, AlienBuilderBotEntity initialBuilder)
    {
        this.world = world;
        this.origin = origin;

        this.sections = new ArrayList<>();
        this.builders = new ArrayList<>();

        this.builders.add(initialBuilder);


        sectionTemplateList = new ArrayList<>();
        InvasionFerroceriumRegistries.BASE_SECTION.iterator().forEachRemaining(sectionTemplateList::add);

        //Create the core of the base
        this.availablePos = new ArrayList<>();
        addBaseSection(BaseSectionTemplates.BASE_CORE, true, new BaseSectPos(0, 0, 0), BlockRotation.NONE);
        baseSectGetAvailablePos();

        createMineshaft();
        mineResourceVein(resources.get(0));
    }

    /// Gets all the positions adjacent to a base section that itself is not occupied by a section.
    private void baseSectGetAvailablePos(){
        for (BaseSection section : sections) {
            baseSectCheckAdjPos(section);
        }
    }

    private void baseSectCheckAdjPos(BaseSection section){
        BaseSectPos pos = section.getOrigin();
        BaseSectPos newPos;

        newPos = pos.add(1, 0, 0);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(-1, 0, 0);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(0, 0, 1);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }

        newPos = pos.add(0, 0, -1);
        if (!availablePos.contains(newPos) && checkSectionLocationClear(newPos)){
            availablePos.add(newPos);
        }
    }

    /// Grows the base at random by adding an extra base section to the base
    public void growBase(){
        int randomInt = random.nextInt(sectionTemplateList.size());
        BaseSectPos offset = availablePos.toArray(new BaseSectPos[0])[random.nextInt(availablePos.size())];
        BlockRotation rot = BlockRotation.NONE;

        addBaseSection(sectionTemplateList.get(randomInt), false, offset, rot);
    }

    private boolean checkSectionLocationClear(BaseSectPos pos){
        for (BaseSection section : sections) {
            if (section.getOrigin().equals(pos)){
                return false;
            }
        }
        return true;
    }

    private void addBaseSection(BaseSectionTemplate sectionTemplate, boolean isCore, BaseSectPos offset, BlockRotation rot){
        BaseSection newSection = new BaseSection(sectionTemplate, world, offset, rot, false);
        sections.add(newSection);
        availablePos.remove(offset);
        baseSectCheckAdjPos(newSection);

        Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
        bot.ifPresent(x -> x.setSection(newSection));
    }

    /// Returns the first alien builder bot to not be building.
    private Optional<AlienBuilderBotEntity> getFirstAvailableAlienBuilderBotEntity(){
        for (AlienBuilderBotEntity builder : builders) {
            if (!(builder.isBuilding() || builder.isGathering())){
                return Optional.of(builder);
            }
        }
        return Optional.empty();
    }

    /// Adds a preexisting builder to the builders
    public void hireBuilder(AlienBuilderBotEntity builder){
        builders.add(builder);
    }

    /// Adds a newly spawned builder to the builders
    public void spawnBuilder(){
        AlienBuilderBotEntity builder = new AlienBuilderBotEntity(world, this);
        builder.refreshPositionAndAngles(Vec3d.of(this.origin), 0, 0);
        hireBuilder(builder);
        this.world.spawnEntity(builder);
    }

    /// Ticks this alien base.
    public void tick(){
        //Grow the base after the timer finishes
        if (baseGrowTime > 0) {
            baseGrowTime--;
        }
        else {
            Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
            if (bot.isEmpty()) {
                spawnBuilder();
            }
            else {
                growBase();
            }
            baseGrowTime = random.nextBetween(3000, 12000);
        }
        //Don't look for things to mine all the time
        if (search_time_count > 0){
            search_time_count--;
        }
        else{
            findResourcesToCollect();
            mineResourceVeins();
            search_time_count = SEARCH_TIME;
        }
    }

    public BlockPos getOrigin(){
        return this.origin;
    }

    public ArrayList<BaseSection> getSections(){
        return this.sections;
    }

    public ArrayList<AlienBuilderBotEntity> getBuilders(){
        return this.builders;
    }

    public UUID getUuid(){
        return this.uuid;
    }

    /// Returns a list of all the chests held within the alien base.
    /// <p>Used to allow alien builder bots to deposit and/or pick up collected items.</p>
    public ArrayList<BlockPos> getChestLocations(){
        throw new NotImplementedException();
    }

    public void setBlockSearchRadius(int minBlockSearchRadius, int maxBlockSearchRadius) {
        this.minBlockSearchRadius = minBlockSearchRadius;
        this.maxBlockSearchRadius = maxBlockSearchRadius;
    }

    /// Creates a mineshaft starting at the middle of the base going down using a spiral staircase.
    /// <p>Uses a strip mining from y-levels 48 (for aluminium and copper), 16 (for iron) and -53 (for diamonds) </p>
    private void createMineshaft(){
        ArrayList<BlockPos> mineshaft = new ArrayList<>();
        for (int y = origin.getY(), i = 0; y >= -53; y--, i++){
            int step = i%8;
            if (step != 7) {
                mineshaft.add(origin.add(-1, -i, -1));
            }
            if (step != 6) {
                mineshaft.add(origin.add(0, -i, -1));
            }
            if (step != 5) {
                mineshaft.add(origin.add(1, -i, -1));
            }
            if (step != 4) {
                mineshaft.add(origin.add(1, -i, 0));
            }
            if (step != 3) {
                mineshaft.add(origin.add(1, -i, 1));
            }
            if (step != 2) {
                mineshaft.add(origin.add(0, -i, 1));
            }
            if (step != 1) {
                mineshaft.add(origin.add(-1, -i, 1));
            }
            if (step != 0) {
                mineshaft.add(origin.add(-1, -i, 0));
            }

            if (STRIP_MINE_LEVELS.contains(y)) {
                mineshaft.add(origin.add(-2,-i,-2));
                mineshaft.add(origin.add(-1,-i,-2));
                mineshaft.add(origin.add(0,-i,-2));
                mineshaft.add(origin.add(1,-i,-2));
                mineshaft.add(origin.add(2,-i,-2));

                mineshaft.add(origin.add(-2,-i,-1));
                mineshaft.add(origin.add(2,-i,-1));

                mineshaft.add(origin.add(-2,-i,0));
                mineshaft.add(origin.add(2,-i,0));

                mineshaft.add(origin.add(-2,-i,1));
                mineshaft.add(origin.add(2,-i,1));

                mineshaft.add(origin.add(-2,-i,2));
                mineshaft.add(origin.add(-1,-i,2));
                mineshaft.add(origin.add(0,-i,2));
                mineshaft.add(origin.add(1,-i,2));
                mineshaft.add(origin.add(2,-i,2));

                createStripMine();
            }
        }
        resources.add(new Vein(mineshaft, true));
        createStairwell();
    }

    void createStairwell() {
        BlockState bs = Blocks.STONE_BRICKS.getDefaultState();
        for (int y = 0, i = 0; y >= -53; y--, i++) {
            int step = i % 8;
            if (step != 7) {
                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, -1), bs));
            }
            if (step != 6) {
                baseBlocks.add(new BaseBlock(new BlockPos(0, -i, -1), bs));
            }
            if (step != 5) {
                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, -1), bs));
            }
            if (step != 4) {
                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, 0), bs));
            }
            if (step != 3) {
                baseBlocks.add(new BaseBlock(new BlockPos(1, -i, 1), bs));
            }
            if (step != 2) {
                baseBlocks.add(new BaseBlock(new BlockPos(0, -i, 1), bs));
            }
            if (step != 1) {
                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, 1), bs));
            }
            if (step != 0) {
                baseBlocks.add(new BaseBlock(new BlockPos(-1, -i, 0), bs));
            }
        }
    }

    private void createStripMine(){

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
                    searchedBlock.set(getOrigin().add(x, y, z));
                    if (resources.stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && blockIsCollectible(searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        resources.add(new Vein(vein));
                    }
                    //-x+z
                    searchedBlock.set(getOrigin().add(-x, y, z));
                    if (resources.stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && blockIsCollectible(searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        resources.add(new Vein(vein));
                        resources.add(new Vein(vein));
                    }
                    //+x-z
                    searchedBlock.set(getOrigin().add(x, y, -z));
                    if (resources.stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && blockIsCollectible(searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        resources.add(new Vein(vein));
                    }
                    //-x-z
                    searchedBlock.set(getOrigin().add(-x, y, -z));
                    if (resources.stream().noneMatch(vein -> vein.contains(searchedBlock.get())) && blockIsCollectible(searchedBlock.get(), EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
                        ArrayList<BlockPos> vein = new ArrayList<>(Arrays.asList(searchedBlock.get()));
                        findAdjacentResourcesToCollect(searchedBlock.get(), vein);
                        resources.add(new Vein(vein));
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
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -x
        searchedBlock = blockPos.add(-1, 0, 0);
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check +y
        searchedBlock = blockPos.add(0, 1, 0);
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -y
        searchedBlock = blockPos.add(0, -1, 0);
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check +z
        searchedBlock = blockPos.add(0, 0, 1);
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
        //Check -z
        searchedBlock = blockPos.add(0, 0, -1);
        if (!resources.contains(searchedBlock) && blockIsCollectible(searchedBlock, EnumSet.of(ResourceCategory.WOOD, ResourceCategory.ORES))){
            resources.add(searchedBlock);
            findAdjacentResourcesToCollect(searchedBlock, resources);
        }
    }

    private void mineResourceVeins(){
        if (resources.isEmpty()) return;
        int randomInt = random.nextInt(resources.size());
        mineResourceVein(resources.remove(randomInt));
    }

    private void mineResourceVein(Vein vein){
        Optional<AlienBuilderBotEntity> bot = getFirstAvailableAlienBuilderBotEntity();
        bot.ifPresent(x -> x.setVein(vein));
    }

    public boolean blockIsCollectible(BlockPos blockPos){
        return COLLECTIBLE_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
    }

    public boolean blockIsCollectible(BlockPos blockPos, EnumSet<ResourceCategory> resCat){
        boolean collectible = false;
        if (resCat.contains(ResourceCategory.ORES)) {
            collectible |= COLLECTIBLE_ORE_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
        }
        if (resCat.contains(ResourceCategory.WOOD)) {
            collectible |= COLLECTIBLE_WOOD_BLOCKS.contains(world.getBlockState(blockPos).getBlock());
        }
        if (resCat.contains(ResourceCategory.STONE)) {
            collectible |= Blocks.STONE == world.getBlockState(blockPos).getBlock();
        }

        return collectible;
    }

    public ArrayList<BaseBlock> getBaseBlocks(){
        return this.baseBlocks;
    }

}
