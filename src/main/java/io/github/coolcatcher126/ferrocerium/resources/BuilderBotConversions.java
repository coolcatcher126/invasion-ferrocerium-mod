package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Map;

public class BuilderBotConversions {
    Map<Item, ArrayList<Item>> recipes;

    public BuilderBotConversions(){
        this.recipes = Map.of();
    }

    public ArrayList<Item> getRequiredItemsToCraft(Item item){
        return this.recipes.get(item);
    }

    public void addRecipe(Item item, ArrayList<Item> resources){
        this.recipes.put(item, resources);
    }
}
