package com.robocraft999.traidingnetwork.resourcepoints.mapper;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.mapper.RecipeTypeMapper;
import com.robocraft999.traidingnetwork.resourcepoints.IngredientMap;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.recipe.BaseRecipeTypeMapper;
import com.robocraft999.traidingnetwork.resourcepoints.nss.NSSItem;
import com.robocraft999.traidingnetwork.resourcepoints.nss.NormalizedSimpleStack;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.*;

public class CreateMapper {
    private abstract static class CreateProcessingRecipeMapper<R extends ProcessingRecipe<?>> extends BaseRecipeTypeMapper<R> {

        @Override
        public String getDescription() {
            return "Create ProcessingRecipeMapper";
        }

        @Override
        public boolean handleRecipeTyped(IMappingCollector<NormalizedSimpleStack, Long> mapper, R recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
            //Outputs
            List<Object> outputs = new ArrayList<>();
            List<ItemStack> results = recipe.getRollableResults().stream().filter(pO -> pO.getChance() >= 1.0f).map(ProcessingOutput::getStack).toList();
            outputs.addAll(results);
            if (outputs.isEmpty()) {
                TraidingNetwork.LOGGER.debug("Recipe ({}) contains no outputs: {}", recipe.getId(), outputs);
                return false;
            }
            NSSOutput bundledOutput = mapOutputs(mapper, recipe, fakeGroupManager, outputs.toArray());

            //Inputs
            NSSInput bundledInput = getInput(recipe, fakeGroupManager);
            if (bundledInput == null || !bundledInput.successful()){
                return addConversionsAndReturn(mapper, bundledInput != null ? bundledInput.fakeGroupMap() : null, true);
            }

            mapper.addConversion(bundledOutput.amount(), bundledOutput.nss(), bundledInput.ingredientMap().getMap());
            return addConversionsAndReturn(mapper, bundledInput.fakeGroupMap(), true);
        }

        private NSSInput getInput(R recipe, INSSFakeGroupManager fakeGroupManager){
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients.isEmpty()) {
                TraidingNetwork.LOGGER.debug("Recipe ({}) contains no inputs: (Ingredients: {})", recipe.getId(), ingredients);
                return null;
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                if (recipe instanceof ItemApplicationRecipe iaRecipe && iaRecipe.shouldKeepHeldItem() && i == 1)
                    continue; // Skip ItemApplicationRecipe's held item if it is not consumed.
                if (!convertIngredient(-1, ingredient, ingredientMap, fakeGroupMap, fakeGroupManager, recipe.getId())) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        protected Collection<Ingredient> getIngredients(R recipe) {
            NonNullList<Ingredient> ingredients = (NonNullList<Ingredient>) super.getIngredients(recipe);
            TraidingNetwork.LOGGER.debug("Recipe ({}) contains following inputs: (Ingredients: {})", recipe.getId(), ingredients.stream().map(Ingredient::getItems).toList());
            if (ingredients.isEmpty()) {
                TraidingNetwork.LOGGER.debug("Recipe ({}) contains no inputs: (Ingredients: {})", recipe.getId(), ingredients);
                return null;
            }
            return ingredients;
        }
    }

    @RecipeTypeMapper(/*requiredMods = "create"*/)
    public static class CreateCrushingMapper extends CreateProcessingRecipeMapper<CrushingRecipe>{

        @Override
        public String getName() {
            return "CrushingMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.CRUSHING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = "create")
    public static class CreateMillingMapper extends CreateProcessingRecipeMapper<MillingRecipe>{

        @Override
        public String getName() {
            return "MillingMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.MILLING.getType();
        }
    }
}
