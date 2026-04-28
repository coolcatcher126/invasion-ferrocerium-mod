package io.github.coolcatcher126.ferrocerium.base;

import io.github.coolcatcher126.ferrocerium.base.ai.*;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import io.github.coolcatcher126.ferrocerium.resources.Vein;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/// A single base.
/// Each base owns one or more Alien Builder Bots.
public class AlienBase {
    BlockPos origin;
    ArrayList<BaseSection> sections;
    ArrayList<AlienBuilderBotEntity> builders;
    AlienBaseTaskScheduler scheduler;
    World world;
    ArrayList<Vein> resources = new ArrayList<>();//Things to mine
    ArrayList<BaseBlock> baseBlocks = new ArrayList<>();//Things to build that are not base sections

    protected final Random random = Random.create();
    UUID uuid = MathHelper.randomUuid(this.random);

    //Recreate alien base
    public AlienBase(World world, BlockPos origin, ArrayList<BaseSection> sections, ArrayList<BaseBlock> baseBlocks, ArrayList<Vein> resources, ArrayList<AlienBuilderBotEntity> builders, UUID uuid){
        this.world = world;
        this.origin = origin;

        sections.forEach(s -> s.setAlienBase(this));

        this.sections = sections;
        this.baseBlocks = baseBlocks;
        this.builders = builders;
        this.uuid = uuid;

        this.resources = resources;

        this.scheduler = new AlienBaseTaskScheduler();

        if (world != null && !world.isClient) {
            initTasks();
        }
    }

    //Create new alien base
    public AlienBase(World world, BlockPos origin, AlienBuilderBotEntity initialBuilder) {
        this.world = world;
        this.origin = origin;

        this.sections = new ArrayList<>();
        this.builders = new ArrayList<>();

        this.builders.add(initialBuilder);

        this.scheduler = new AlienBaseTaskScheduler();
        if (world != null && !world.isClient) {
            initTasks();
        }
    }

    private void initTasks(){
        this.scheduler.add(new ExpandWorkforce(this));
        this.scheduler.add(new CreateMineshaft(this));
        this.scheduler.add(new ScanForResources(this));
        this.scheduler.add(new BaseGrower(this));
        this.scheduler.add(new BuilderCommander(this));
    }

    /// Returns the first alien builder bot to not be building.
    public Optional<AlienBuilderBotEntity> getFirstAvailableAlienBuilderBotEntity(){
        for (AlienBuilderBotEntity builder : builders) {
            if (!(builder.isBuilding() || builder.isGathering() || builder.isMining())) {
                return Optional.of(builder);
            }
        }
        return Optional.empty();
    }

    /// Ticks this alien base.
    public void tick(){
        this.world.getProfiler().push("alien_base_tick");

        this.scheduler.tick();

        this.world.getProfiler().pop();
    }

    public BlockPos getOrigin(){
        return this.origin;
    }

    public ArrayList<BaseSection> getSections(){
        return this.sections;
    }

    public void addBaseSection(BaseSection section){
        this.sections.add(section);
    }

    public ArrayList<AlienBuilderBotEntity> getBuilders(){
        return this.builders;
    }

    /// Adds a preexisting builder to the builders
    public void hireBuilder(AlienBuilderBotEntity builder){
        builders.add(builder);
    }

    public UUID getUuid(){
        return this.uuid;
    }

    /// Returns a list of all the chests held within the alien base.
    /// <p>Used to allow alien builder bots to deposit and/or pick up collected items.</p>
    public ArrayList<BlockPos> getChestLocations(){
        throw new NotImplementedException();
    }



    public ArrayList<BaseBlock> getBaseBlocks(){
        return this.baseBlocks;
    }

    public void addBaseBlock(BaseBlock block){
        this.baseBlocks.add(block);
    }

    public ArrayList<Vein> getResources(){
        return  this.resources;
    }

    public void addVein(Vein vein){
        resources.add(vein);
    }

    public void addVeinFirst(Vein vein){
        resources.addFirst(vein);
    }

    public Vein removeClosestVein(BlockPos targetPos){
        int vein = 0;
        for (Vein resource : resources) {
            if (resources.get(vein).getClosest(targetPos).getSquaredDistance(targetPos) > resource.getClosest(targetPos).getSquaredDistance(targetPos)) {
                vein = resources.indexOf(resource);
            }
        }
        return resources.remove(vein);
    }

    public RegistryKey<World> getDimension(){
        return this.world.getRegistryKey();
    }

    public World getWorld(){
        return this.world;
    }

    public Random getRandom(){
        return random;
    }

}
