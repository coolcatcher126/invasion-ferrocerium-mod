package io.github.coolcatcher126.ferrocerium;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.coolcatcher126.ferrocerium.entity.custom.*;
import io.github.coolcatcher126.ferrocerium.registries.InvasionFerroceriumRegistries;
import io.github.coolcatcher126.ferrocerium.base.BaseSectionTemplates;
import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.components.InvasionFerroceriumComponents;
import io.github.coolcatcher126.ferrocerium.components.InvasionLevelComponent;
import io.github.coolcatcher126.ferrocerium.entity.ModEntities;
import io.github.coolcatcher126.ferrocerium.item.ModItemGroups;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import io.github.coolcatcher126.ferrocerium.sound.ModSounds;
import io.github.coolcatcher126.ferrocerium.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;

import static net.minecraft.server.command.CommandManager.*;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvasionFerrocerium implements ModInitializer {
	public static final String MOD_ID = "invasion-ferrocerium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        InvasionFerroceriumRegistries.buildRegistries();

		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModSounds.registerModSounds();
		ModEntities.registerModEntities();

		ModWorldGeneration.GenerateModWorldGen();
		
		FabricDefaultAttributeRegistry.register(ModEntities.ANT_SCOUT_BOT, AntScoutBotEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANT_SOLDIER_BOT, AntSoldierBotEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ALIEN_HELICOPTER_BOT, AlienHelicopterBotEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ALIEN_BUILDER_BOT, AlienBuilderBotEntity.createAttributes());

        BaseSectionTemplates.registerBaseSections();

        //Make invasion automatically start after x amount of time.
        ServerTickEvents.END_WORLD_TICK.register((world) ->
        {
            //TODO: Make the number of days configurable
            int days = 5;
            if (InvasionFerroceriumComponents.getInvasionLevel(world) == 0 && world.getTime() == 24000 * days) {
                InvasionFerroceriumComponents.setInvasion(world, 1);
            }
        });

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((serverWorld, entity, livingEntity) -> {
            if (entity instanceof InvasionBotEntity){
                InvasionFerroceriumComponents.progressInvasion(serverWorld);
            }
        });

        //Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("invasion")
                        .then(literal("get")
                                .executes(context -> // /invasion get
                                        executeGetInvasion(context.getSource().getWorld(), context)
                                )
                                .then(argument("world", DimensionArgumentType.dimension()) // /invasion get <world>
                                        .executes(context ->
                                                executeGetInvasion(DimensionArgumentType.getDimensionArgument(context, "world"), context)
                                        )
                                )
                        )
                        .then(literal("progress")
                                .executes(context -> // /invasion progress
                                        executeProgressInvasion(context.getSource().getWorld(), context)
                                )
                                .then(argument("world", DimensionArgumentType.dimension()) // /invasion progress <world>
                                        .executes(context ->
                                                executeProgressInvasion(DimensionArgumentType.getDimensionArgument(context, "world"), context)
                                        )
                                )
                        )
                        .then(literal("fight")
                                .executes(context -> // /invasion fight
                                        executeFightInvasion(context.getSource().getWorld(), context)
                                )
                                .then(argument("world", DimensionArgumentType.dimension()) // /invasion fight <world>
                                        .executes(context ->
                                                executeFightInvasion(DimensionArgumentType.getDimensionArgument(context, "world"), context)
                                        )
                                )
                        )
                        .then(literal("set")
                                .then(argument("level", IntegerArgumentType.integer(0, InvasionLevelComponent.MAX_INVASION))
                                        .executes(context -> // /invasion set <level>
                                                executeSetInvasion(context.getSource().getWorld(), context)
                                        )
                                        .then(argument("world", DimensionArgumentType.dimension()) // /invasion set <level> <world>
                                                .executes(context ->
                                                        executeSetInvasion(DimensionArgumentType.getDimensionArgument(context, "world"), context)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int executeGetInvasion(World world, CommandContext<ServerCommandSource> context){
        int invLevel = InvasionFerroceriumComponents.getInvasionLevel(world);
        context.getSource().sendFeedback(() -> Text.literal("Invasion level is: %s".formatted(invLevel)), false);
        return 1;
    }

    private static int executeProgressInvasion(World world, CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> Text.literal("Invasion level has increased by 1"), false);
        InvasionFerroceriumComponents.progressInvasion(world);
        return executeGetInvasion(world, context);
    }

    private static int executeFightInvasion(World world, CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> Text.literal("Invasion level has decreased by 1"), false);
        InvasionFerroceriumComponents.fightBackInvasion(world);
        return executeGetInvasion(world, context);
    }

    private static int executeSetInvasion(World world, CommandContext<ServerCommandSource> context){
        final int level = IntegerArgumentType.getInteger(context, "level");
        InvasionFerroceriumComponents.setInvasion(world, level);
        return executeGetInvasion(world, context);
    }
}