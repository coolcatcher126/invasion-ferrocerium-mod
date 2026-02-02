package io.github.coolcatcher126.ferrocerium.entity.goal;

import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.recipe.CraftingRecipe;

import java.util.ArrayList;

/// Lets the mob convert one resource into another.
public class CraftGoal extends Goal {
    Inventory inventory;
    AlienBuilderBotEntity bot;
    ArrayList<Item> itemsToCraft;


    public CraftGoal(AlienBuilderBotEntity bot, Inventory inventory){
        this.bot = bot;
        this.inventory = inventory;
    }

    @Override
    public boolean canStart() {
        return !itemsToCraft.isEmpty();
    }

    @Override
    public void tick() {
        //TODO: Craft the gathered raw materials into the requested usable materials
        for (Item item : itemsToCraft) {

        }
    }
}
