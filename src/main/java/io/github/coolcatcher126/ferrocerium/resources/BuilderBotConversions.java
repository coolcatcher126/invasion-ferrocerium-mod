package io.github.coolcatcher126.ferrocerium.resources;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuilderBotConversions {
    Map<Item, Map<Item, Integer>> recipes;

    public BuilderBotConversions(){
        this.recipes = new HashMap<>();
    }

    public Map<Item, Integer> getRequiredItemsToCraft(Item item){
        if (this.recipes.containsKey(item)) {
            return this.recipes.get(item);
        }
        return null;
    }

    public boolean canCraft(Item item, Inventory sourceInventory){
        Map<Item, Integer> ingredients = getRequiredItemsToCraft(item);
        if (ingredients == null){
            return false;
        }
        for (Map.Entry<Item, Integer> ingredientType : ingredients.entrySet()){
            if (ingredientType.getValue() > sourceInventory.count(ingredientType.getKey())){
                return false;
            }
        }
        return true;
    }

    public void addRecipe(Item item, Map<Item, Integer> resources){
        this.recipes.put(item, resources);
    }
}
