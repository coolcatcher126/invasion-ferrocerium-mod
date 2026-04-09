package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

public class BuilderBotConversionsTest {
    public static final BuilderBotConversions RECIPES = new BuilderBotConversions();
    private static SimpleInventory INVENTORY;

    @BeforeAll
    static void beforeAll(){
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        INVENTORY = new SimpleInventory(9);
        INVENTORY.addStack(new ItemStack(Items.STONE, 3));
        RECIPES.addRecipe(Items.STONE_BRICKS, Map.of(Items.STONE, 1));
        RECIPES.addRecipe(Items.CHEST, Map.of(Items.OAK_WOOD, 2));
        RECIPES.addRecipe(Items.IRON_BARS, Map.of(Items.IRON_ORE, 1));
        RECIPES.addRecipe(Items.STONE_BRICK_STAIRS, Map.of(Items.STONE_BRICKS, 1));
    }

    @Test
    void testGetRequiredItemsToCraft(){
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(Items.STONE_BRICKS), Map.of(Items.STONE, 1));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(Items.CHEST), Map.of(Items.OAK_WOOD, 2));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(Items.IRON_BARS), Map.of(Items.IRON_ORE, 1));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(Items.STONE_BRICK_STAIRS), Map.of(Items.STONE_BRICKS, 1));
        Assertions.assertNull(RECIPES.getRequiredItemsToCraft(Items.ACACIA_FENCE));
    }

    @Test
    void testGetReqItemsToCraftRec() {
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(Items.STONE_BRICKS), Map.of(Items.STONE, 1));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(Items.CHEST), Map.of(Items.OAK_WOOD, 2));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(Items.IRON_BARS), Map.of(Items.IRON_ORE, 1));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(Items.STONE_BRICK_STAIRS), Map.of(Items.STONE, 1, Items.STONE_BRICKS, 1));
        Assertions.assertNull(RECIPES.getReqItemsToCraftRec(Items.ACACIA_FENCE));
    }

    @Test
    void testGetCraftingStepsForItem(){
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(Items.STONE_BRICKS, Optional.empty()), Map.of(Items.STONE_BRICKS, 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(Items.STONE_BRICKS, Optional.of(5)), Map.of(Items.STONE_BRICKS, 5));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(Items.CHEST, Optional.empty()), Map.of(Items.CHEST, 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(Items.IRON_BARS, Optional.empty()), Map.of(Items.IRON_BARS, 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(Items.STONE_BRICK_STAIRS, Optional.empty()), Map.of(Items.STONE_BRICK_STAIRS, 1, Items.STONE_BRICKS, 1));
        Assertions.assertNull(RECIPES.getCraftingStepsForItem(Items.ACACIA_FENCE, Optional.empty()));
    }

    @Test
    void testCanCraft(){
        Assertions.assertTrue(RECIPES.canCraft(Items.STONE_BRICKS, INVENTORY));
        Assertions.assertFalse(RECIPES.canCraft(Items.CHEST, INVENTORY));
    }
}
