package io.github.coolcatcher126.ferrocerium.resources;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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
        RECIPES.addRecipe(ItemVariant.of(Items.STONE_BRICKS), Map.of(ItemVariant.of(Items.STONE), 1));
        RECIPES.addRecipe(ItemVariant.of(Items.CHEST), Map.of(ItemVariant.of(Items.OAK_WOOD), 2));
        RECIPES.addRecipe(ItemVariant.of(Items.IRON_BARS), Map.of(ItemVariant.of(Items.IRON_ORE), 1));
        RECIPES.addRecipe(ItemVariant.of(Items.STONE_BRICK_STAIRS), Map.of(ItemVariant.of(Items.STONE_BRICKS), 1));
    }

    @Test
    void testGetRequiredItemsToCraft(){
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(ItemVariant.of(Items.STONE_BRICKS)), Map.of(ItemVariant.of(Items.STONE), 1));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(ItemVariant.of(Items.CHEST)), Map.of(ItemVariant.of(Items.OAK_WOOD), 2));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(ItemVariant.of(Items.IRON_BARS)), Map.of(ItemVariant.of(Items.IRON_ORE), 1));
        Assertions.assertEquals(RECIPES.getRequiredItemsToCraft(ItemVariant.of(Items.STONE_BRICK_STAIRS)), Map.of(ItemVariant.of(Items.STONE_BRICKS), 1));
        Assertions.assertNull(RECIPES.getRequiredItemsToCraft(ItemVariant.of(Items.ACACIA_FENCE)));
    }

    @Test
    void testGetReqItemsToCraftRec() {
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(ItemVariant.of(Items.STONE_BRICKS)), Map.of(ItemVariant.of(Items.STONE), 1));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(ItemVariant.of(Items.CHEST)), Map.of(ItemVariant.of(Items.OAK_WOOD), 2));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(ItemVariant.of(Items.IRON_BARS)), Map.of(ItemVariant.of(Items.IRON_ORE), 1));
        Assertions.assertEquals(RECIPES.getReqItemsToCraftRec(ItemVariant.of(Items.STONE_BRICK_STAIRS)), Map.of(ItemVariant.of(Items.STONE), 1, ItemVariant.of(Items.STONE_BRICKS), 1));
        Assertions.assertNull(RECIPES.getReqItemsToCraftRec(ItemVariant.of(Items.ACACIA_FENCE)));
    }

    @Test
    void testGetCraftingStepsForItem(){
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.STONE_BRICKS), Optional.empty()), Map.of(ItemVariant.of(Items.STONE_BRICKS), 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.STONE_BRICKS), Optional.of(5)), Map.of(ItemVariant.of(Items.STONE_BRICKS), 5));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.CHEST), Optional.empty()), Map.of(ItemVariant.of(Items.CHEST), 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.IRON_BARS), Optional.empty()), Map.of(ItemVariant.of(Items.IRON_BARS), 1));
        Assertions.assertEquals(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.STONE_BRICK_STAIRS), Optional.empty()), Map.of(ItemVariant.of(Items.STONE_BRICK_STAIRS), 1, ItemVariant.of(Items.STONE_BRICKS), 1));
        Assertions.assertNull(RECIPES.getCraftingStepsForItem(ItemVariant.of(Items.ACACIA_FENCE), Optional.empty()));
    }

    @Test
    void testCanCraft(){
        Assertions.assertTrue(RECIPES.canCraft(ItemVariant.of(Items.STONE_BRICKS), INVENTORY));
        Assertions.assertFalse(RECIPES.canCraft(ItemVariant.of(Items.CHEST), INVENTORY));
    }
}
