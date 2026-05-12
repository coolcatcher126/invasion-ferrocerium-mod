package io.github.coolcatcher126.ferrocerium.entity.ai.goal;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Lets the mob convert one resource into another.
public class CraftGoal extends Goal {
    AlienBuilderBotEntity bot;
    List<Item> itemsToCraft;

    public CraftGoal(AlienBuilderBotEntity bot){
        this.bot = bot;
        itemsToCraft = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        return !itemsToCraft.isEmpty();
    }

    @Override
    public void tick() {
        Map<Item, Integer> itemsRequired;
        for (Item item : itemsToCraft) {
            if (InvasionFerrocerium.RECIPES.canCraft(item, bot.getInventory())) {
                itemsRequired = InvasionFerrocerium.RECIPES.getRequiredItemsToCraft(item);
                for (Map.Entry<Item, Integer> ingredientType : itemsRequired.entrySet()) {
                    int count = ingredientType.getValue();
                    for (int i = 0; i < bot.getInventory().size(); i++) {
                        ItemStack stack = bot.getInventory().getStack(i);

                        if (stack.getItem() == ingredientType.getKey()) {
                            if (stack.getCount() <= count) {
                                count -= stack.getCount();
                                bot.getInventory().setStack(i, ItemStack.EMPTY);
                                bot.getInventory().addStack(item.getDefaultStack());
                            } else {
                                stack.setCount(stack.getCount() - count);
                                count = 0;
                            }
                        }

                        if (count <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addCraftingRequest(Item requestedItem, int count){
        for (int i = 0; i < count; i++) {
            itemsToCraft.add(requestedItem);
        }
    }

    public void addCraftingRequest(Item requestedItem){
        addCraftingRequest(requestedItem, 1);
    }

}
