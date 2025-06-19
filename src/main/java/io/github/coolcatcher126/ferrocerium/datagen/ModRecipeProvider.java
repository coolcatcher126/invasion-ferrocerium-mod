package io.github.coolcatcher126.ferrocerium.datagen;

import io.github.coolcatcher126.ferrocerium.block.ModBlocks;
import io.github.coolcatcher126.ferrocerium.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        //Smelt Aluminum to ingots
        List<ItemConvertible> ALUMINUM_SMELTABLES = List.of(ModItems.RAW_ALUMINUM, ModBlocks.ALUMINUM_ORE_BLOCK, ModBlocks.DEEPSLATE_ALUMINUM_ORE_BLOCK);
         offerSmelting(recipeExporter, ALUMINUM_SMELTABLES, RecipeCategory.MISC, ModItems.ALUMINUM_INGOT, 0.25F, 200, "aluminum_ingot");
         offerBlasting(recipeExporter, ALUMINUM_SMELTABLES, RecipeCategory.MISC, ModItems.ALUMINUM_INGOT, 0.25F, 100, "aluminum_ingot");

         //Compact to block and back
         offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.ALUMINUM_INGOT, RecipeCategory.MISC, ModBlocks.ALUMINUM_BLOCK);
         offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.RAW_ALUMINUM, RecipeCategory.MISC, ModBlocks.RAW_ALUMINUM_BLOCK);
         offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.STRANGE_ALLOY_INGOT, RecipeCategory.MISC, ModBlocks.STRANGE_ALLOY_BLOCK);

         //Make thermite
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModItems.THERMITE, 2)
                .input(Items.IRON_INGOT)
                .input(ModItems.ALUMINUM_INGOT)
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_INGOT), FabricRecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .criterion(FabricRecipeProvider.hasItem(ModItems.ALUMINUM_INGOT), FabricRecipeProvider.conditionsFromItem(ModItems.ALUMINUM_INGOT))
                .offerTo(recipeExporter);

        //Make tools
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.STRANGE_ALLOY_SWORD)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .input('I', ModItems.STRANGE_ALLOY_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.STRANGE_ALLOY_INGOT), conditionsFromItem(ModItems.STRANGE_ALLOY_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.STRANGE_ALLOY_PICKAXE)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', ModItems.STRANGE_ALLOY_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.STRANGE_ALLOY_INGOT), conditionsFromItem(ModItems.STRANGE_ALLOY_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.STRANGE_ALLOY_AXE)
                .pattern("II ")
                .pattern("IS ")
                .pattern(" S ")
                .input('I', ModItems.STRANGE_ALLOY_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.STRANGE_ALLOY_INGOT), conditionsFromItem(ModItems.STRANGE_ALLOY_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.STRANGE_ALLOY_SHOVEL)
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', ModItems.STRANGE_ALLOY_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.STRANGE_ALLOY_INGOT), conditionsFromItem(ModItems.STRANGE_ALLOY_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.STRANGE_ALLOY_HOE)
                .pattern("II ")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', ModItems.STRANGE_ALLOY_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.STRANGE_ALLOY_INGOT), conditionsFromItem(ModItems.STRANGE_ALLOY_INGOT))
                .offerTo(recipeExporter);
    }
}
