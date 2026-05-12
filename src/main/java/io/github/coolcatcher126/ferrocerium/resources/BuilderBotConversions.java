package io.github.coolcatcher126.ferrocerium.resources;

import com.google.common.collect.Maps;
import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderBotConversions {
    Map<Item, Map<Item, Integer>> recipes;

    public BuilderBotConversions(){
        this.recipes = new HashMap<>();
    }

    /// Returns the item and all the ingredients that also need to be crafted
    public Map<Item, Integer> getCraftingStepsForItem(Item item, Optional<Integer> quantity){
        @Nullable Map<Item, Integer> items = null;
        Map<Item, Integer> ingredients = getRequiredItemsToCraft(item);
        if (ingredients != null) {
            items = new HashMap<>();
            items.put(item, quantity.orElse(1));
            for (Map.Entry<Item, Integer> ingredientType : ingredients.entrySet()) {
                Map<Item, Integer> craftingStepsForItem = getCraftingStepsForItem(ingredientType.getKey(), Optional.of(ingredientType.getValue() * quantity.orElse(1)));
                if (craftingStepsForItem == null){
                    break;
                }
                items = Stream.of(items, craftingStepsForItem)
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Integer::sum
                        ));
            }
        }
        return items;
    }

    /// Returns the items the AlienBuilderBot needs to craft an item recursively
    public Map<Item, Integer> getReqItemsToCraftRec(Item item){
        Map<Item, Integer> ingredients = getRequiredItemsToCraft(item);
        if (ingredients != null) {
            for (Map.Entry<Item, Integer> ingredientType : ingredients.entrySet()) {
                Map<Item, Integer> subIngredients = getReqItemsToCraftRec(ingredientType.getKey());
                if (subIngredients == null){
                    break;
                }
                subIngredients = Maps.newHashMap(subIngredients);
                subIngredients.replaceAll((ingredient, count) -> count * ingredientType.getValue());
                ingredients = Stream.of(ingredients, subIngredients)
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Integer::sum
                        ));
            }
        }
        return ingredients;
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

    public void craftRequiredResources(AlienBuilderBotEntity bot, List<Item> requiredResources){
        Map<Item, Long> resMap = requiredResources.stream().collect(Collectors.groupingBy(Item::asItem, Collectors.counting()));
        for (Map.Entry<Item, Long> resEntry : resMap.entrySet()) {
            Map<Item, Integer> craftingSteps = InvasionFerrocerium.RECIPES.getCraftingStepsForItem(resEntry.getKey().asItem(), Optional.of(Math.toIntExact(resEntry.getValue())));
            if (null != craftingSteps) {
                for (Map.Entry<Item, Integer> resEntry2 : craftingSteps.entrySet()) {
                    bot.addCraftingRequest(resEntry2.getKey(), Math.toIntExact(resEntry2.getValue()));
                }
            }
        }
    }
}

