package io.github.coolcatcher126.ferrocerium.world.gen;

public class ModWorldGeneration {
    public static void GenerateModWorldGen(){
        ModOreGeneration.generateOres();
        ModEntitySpawn.addSpawns();
    }
}
