package com.robocraft999.traidingnetwork.resourcepoints.mapper.recipe;

import com.robocraft999.traidingnetwork.api.mapper.RecipeTypeMapper;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.traidingnetwork.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.*;

import java.util.Collection;
import java.util.List;

@RecipeTypeMapper(priority = Integer.MIN_VALUE)
public class FallbackRecipeTypeMapper extends BaseRecipeTypeMapper<Recipe<?>> {

    @Override
    public String getName() {
        return "FallbackRecipeType";
    }

    @Override
    public String getDescription() {
        return "Fallback for default handling of recipes that extend ICraftingRecipe, AbstractCookingRecipe, SingleItemRecipe, or SmithingRecipe. "
                + "This will catch modded extensions of the vanilla recipe classes, and if the VanillaRecipeTypes mapper is disabled, "
                + "this mapper will still catch the vanilla recipes.";
    }

    @Override
    public boolean canHandle(RecipeType<?> recipeType) {
        //Pretend that we can handle
        return true;
    }

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Recipe<?> recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
        if (recipe instanceof CraftingRecipe || recipe instanceof AbstractCookingRecipe || recipe instanceof SingleItemRecipe ||
                //Note: We may be able to do SmithingRecipe instead of checking these two subtypes, but we likely won't be able to retrieve the ingredients
                recipe instanceof SmithingTransformRecipe || recipe instanceof SmithingTrimRecipe) {
            return super.handleRecipe(mapper, recipe, registryAccess, fakeGroupManager);
        }
        return false;
    }

    @Override
    protected Collection<Ingredient> getIngredients(Recipe<?> recipe) {
        Collection<Ingredient> ingredients = super.getIngredients(recipe);
        if (ingredients.isEmpty()) {
            //If the extension of upgrade recipe doesn't override getIngredients (just like vanilla doesn't)
            // grab the values from the recipe's object itself
            if (recipe instanceof SmithingTransformRecipe transformRecipe) {
                return List.of(transformRecipe.base, transformRecipe.addition, transformRecipe.template);
            } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
                return List.of(trimRecipe.base, trimRecipe.addition, trimRecipe.template);
            }
        }
        return ingredients;
    }

}