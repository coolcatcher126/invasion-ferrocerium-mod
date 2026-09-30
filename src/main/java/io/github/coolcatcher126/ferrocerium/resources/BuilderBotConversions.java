package io.github.coolcatcher126.ferrocerium.resources;

import io.github.coolcatcher126.ferrocerium.InvasionFerrocerium;
import io.github.coolcatcher126.ferrocerium.entity.custom.AlienBuilderBotEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuilderBotConversions {
    Map<ItemVariant, Map<ItemVariant, Integer>> recipes;

    public BuilderBotConversions(){
        this.recipes = new HashMap<>();
    }

    public void addRecipe(ItemVariant item, Map<ItemVariant, Integer> resources){
        this.recipes.put(item, resources);
    }

    /// Returns the item and all the ingredients that also need to be crafted
    public Map<ItemVariant, Integer> getCraftingStepsForItem(ItemVariant item, Optional<Integer> quantity){
        @Nullable Map<ItemVariant, Integer> items = null;
        Map<ItemVariant, Integer> ingredients = getRequiredItemsToCraft(item);
        if (ingredients != null) {
            items = new HashMap<>();
            items.put(item, quantity.orElse(1));
            for (Map.Entry<ItemVariant, Integer> ingredientType : ingredients.entrySet()) {
                Map<ItemVariant, Integer> craftingStepsForItem = getCraftingStepsForItem(ingredientType.getKey(), Optional.of(ingredientType.getValue() * quantity.orElse(1)));
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


    public Map<ItemVariant, Integer> getRequiredItemsToCraft(ItemVariant item){
        if (this.recipes.containsKey(item)) {
            return this.recipes.get(item);
        }
        return null;
    }

    public void requestCraftRequiredResources(AlienBuilderBotEntity bot, List<ItemVariant> requiredResources){
        Map<ItemVariant, Long> resMap = requiredResources.stream().collect(Collectors.groupingBy(itemVariant -> itemVariant, Collectors.counting()));
        for (Map.Entry<ItemVariant, Long> resEntry : resMap.entrySet()) {
            Map<ItemVariant, Integer> craftingSteps = InvasionFerrocerium.RECIPES.getCraftingStepsForItem(resEntry.getKey(), Optional.of(Math.toIntExact(resEntry.getValue())));
            if (null != craftingSteps) {
                for (Map.Entry<ItemVariant, Integer> resEntry2 : craftingSteps.entrySet()) {
                    bot.addCraftingRequest(resEntry2.getKey(), Math.toIntExact(resEntry2.getValue()));
                }
            }
        }
    }

    public boolean tryCraftItem(ItemVariant itemToCraft, InventoryStorage inventoryStorage){
        boolean success = false;
        Map<ItemVariant, Integer> requiredItemsToCraft = getRequiredItemsToCraft(itemToCraft);
        try (Transaction t1 = Transaction.openOuter()) {
            requiredItemsToCraft.forEach((item, count) -> {
                inventoryStorage.extract((item),count, t1);
            });
            inventoryStorage.insert(itemToCraft, 1, t1);
            t1.commit();
            success = true;
        }
        return success;
    }
}

