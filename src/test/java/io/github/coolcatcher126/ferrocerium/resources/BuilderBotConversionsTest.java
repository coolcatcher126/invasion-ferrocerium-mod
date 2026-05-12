package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
    void testCanCraft(){
        Assertions.assertTrue(RECIPES.canCraft(Items.STONE_BRICKS, INVENTORY));
        Assertions.assertFalse(RECIPES.canCraft(Items.CHEST, INVENTORY));
    }
}
