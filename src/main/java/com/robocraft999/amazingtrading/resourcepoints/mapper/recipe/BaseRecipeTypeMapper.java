package com.robocraft999.amazingtrading.resourcepoints.mapper.recipe;

import com.mojang.logging.LogUtils;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.resourcepoints.IngredientMap;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSFluid;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSItem;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import com.robocraft999.amazingtrading.utils.ItemHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//TODO: Fix recipe mapping for things containing RP not working properly? (aka full klein stars)
// We probably could do it with a set value before, make it a grouping of a fake stack that has
// a specific rp value, and it, and then use that? We probably should check the capability for
// it though it might be enough for now to just use an instanceof?
//TODO: Evaluate using a fake crafting inventory and then calling recipe#getRemainingItems? May not be worthwhile to do
// The bigger question is how would the "fake group" stuff work for it? Maybe have an NSSFake called "inverted" that
// gets thrown in with a bucket? Or conversion NSSFake # = inverted + thing
// Alternatively we should have the fake group manager keep track of an intermediary object that says what kind
// of transformations actually is happening so that we can then basically compare sets/easier allow for custom objects
// to do things
public abstract class BaseRecipeTypeMapper<R extends Recipe<?>> implements IRecipeTypeMapper {

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Recipe<?> recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
        return handleRecipeTyped(mapper, (R)recipe, registryAccess, fakeGroupManager);
    }

    public boolean handleRecipeTyped(IMappingCollector<NormalizedSimpleStack, Long> mapper, R recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
        //Outputs
        ItemStack result = recipe.getResultItem(registryAccess);

        if (result.isEmpty()) {
            AmazingTrading.LOGGER.debug("Recipe ({}) contains no outputs: {}", recipe.getId(), result);
            return false;
        }
        NSSOutput bundledOutput = mapOutputs(mapper, recipe, fakeGroupManager, result);

        //Inputs
        NSSInput bundledInput = getInput(recipe, fakeGroupManager);
        if (bundledInput == null || !bundledInput.successful()){
            return addConversionsAndReturn(mapper, bundledInput != null ? bundledInput.fakeGroupMap() : null, true);
        }

        mapper.addConversion(bundledOutput.amount(), bundledOutput.nss(), bundledInput.ingredientMap().getMap());
        return addConversionsAndReturn(mapper, bundledInput.fakeGroupMap(), true);
    }

    public record NSSOutput(int amount, NormalizedSimpleStack nss){}

    public NSSOutput mapOutputs(IMappingCollector<NormalizedSimpleStack, Long> mapper, R recipe, INSSFakeGroupManager fakeGroupManager, Object... allOutputs){
        List<Object> outputs = Arrays.asList(allOutputs);
        if (allOutputs.length == 1 && allOutputs[0] instanceof Collection) {
            outputs = new ArrayList<>((Collection<?>) allOutputs[0]);
        }

        // Assume output stacks will be the size length as outputs
        Map<NormalizedSimpleStack, Integer> outputStacks = new HashMap<>(outputs.size());

        int totalOutputs = 0;
        for (Object output : outputs) {
            if (output == null) continue;

            if (output instanceof ItemStack item) {
                if (item.isEmpty()) continue;

                outputStacks.put(NSSItem.createItem(item), item.getCount());
                totalOutputs += item.getCount();
            } else if (output instanceof FluidStack fluid) {
                if (fluid.isEmpty()) continue;

                outputStacks.put(NSSFluid.createFluid(fluid), fluid.getAmount());
                totalOutputs += fluid.getAmount();
            } else {
                AmazingTrading.LOGGER.warn("Recipe ({}) has unsupported output: {}. Skipping...", recipe.getId(), output);
            }
        }

        NormalizedSimpleStack dummy = fakeGroupManager.getOrCreateFakeGroup(outputStacks.keySet()).getA();

        for (Map.Entry<NormalizedSimpleStack, Integer> entry : outputStacks.entrySet()) {
            mapper.addConversion(entry.getValue(), entry.getKey(), getDummyMap(dummy, entry.getValue()));
        }

        return new NSSOutput(totalOutputs, dummy);
    }

    public record NSSInput(IngredientMap<NormalizedSimpleStack> ingredientMap,
                           List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap,
                           boolean successful){}

    protected NSSInput getInput(R recipe, INSSFakeGroupManager fakeGroupManager){
        Collection<Ingredient> ingredients = getIngredientsChecked(recipe);

        if (ingredients.isEmpty()) {
            AmazingTrading.LOGGER.debug("Recipe ({}) contains no inputs: (Ingredients: {})", recipe.getId(), ingredients);
            return null;
        }

        // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

        for (Ingredient ingredient : ingredients) {
            if (!convertIngredient(-1, ingredient, ingredientMap, fakeGroupMap, fakeGroupManager, recipe.getId())) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }
        }
        return new NSSInput(ingredientMap, fakeGroupMap, true);
    }

    /**
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy) {
        return getDummyMap(dummy, 1);
    }

    /**
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy, int amount) {
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
        ingredientMap.addIngredient(dummy, amount);
        return ingredientMap.getMap();
    }

    /**
     * This method can be used as a helper method to return a specific value and add any existing group conversions. It is important that we add any valid group
     * conversions that we have, regardless of whether the recipe as a whole is valid, because we only create one instance of our group's NSS representation so even if
     * parts of the recipe are not valid, the conversion may be valid and exist in another recipe.
     */
    protected boolean addConversionsAndReturn(IMappingCollector<NormalizedSimpleStack, Long> mapper,
                                            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos, boolean returnValue) {
        if (dummyGroupInfos == null){
            return false;
        }
        //If we have any conversions make sure to add them even if we are returning early
        for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
            for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
                mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
            }
        }
        return returnValue;
    }

    public boolean convertIngredient(int amount, Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap, List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap, INSSFakeGroupManager fakeGroupManager, ResourceLocation recipeID) {
        ItemStack[] matches = getMatchingStacks(ingredient, recipeID);
        if (matches == null) {
            return false;
        } else if (matches.length == 1) {
            //Handle this ingredient as a direct representation of the stack it represents
            return !addIngredient(ingredientMap, getStack(matches[0], amount), recipeID);
        } else if (matches.length > 0) {
            Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack match : matches) {
                //Validate it is not an empty stack in case mods do weird things in custom ingredients
                if (!match.isEmpty()) {
                    rawNSSMatches.add(NSSItem.createItem(match));
                    stacks.add(match);
                }
            }

            int count = stacks.size();
            if (count == 1) {
                return !addIngredient(ingredientMap, getStack(stacks.get(0), amount), recipeID);
            } else if (count > 1) {
                //Handle this ingredient as the representation of all the stacks it supports
                Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                NormalizedSimpleStack dummy = group.getA();
                ingredientMap.addIngredient(dummy, Math.max(amount, 1));
                if (group.getB()) {
                    //Only lookup the matching stacks for the group with conversion if we don't already have
                    // a group created for this dummy ingredient
                    // Note: We soft ignore cases where it fails/there are no matching group ingredients
                    // as then our fake ingredient will never actually have an rp value assigned with it
                    // so the recipe won't either
                    List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                    for (ItemStack stack : stacks) {
                        IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                        if (addIngredient(groupIngredientMap, stack.copy(), recipeID)) {
                            return false;
                        }
                        groupIngredientMaps.add(groupIngredientMap);
                    }
                    fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                }
            }
        }
        return true;
    }

    /**
     * @param item
     * @param amount
     * @return
     */
    public ItemStack getStack(ItemStack item, int amount) {
        if (amount > 0) {
            return new ItemStack(item.getItem(), amount);
        }
        return item.copy();
    }

    @Nullable
    private ItemStack[] getMatchingStacks(Ingredient ingredient, ResourceLocation recipeID) {
        try {
            return ingredient.getItems();
        } catch (Exception e) {
            if (isTagException(e)) {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks "
                                + "due to not properly deserializing and handling tags. Please report this to the ingredient's creator.",
                        recipeID, ingredient.getClass().getName(), e);
            } else {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. "
                        + "Please report this to the ingredient's creator.", recipeID, ingredient.getClass().getName(), e);
            }
            return null;
        }
    }

    //Returns true if it failed and is invalid
    private boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, ResourceLocation recipeID) {
        Item item = stack.getItem();
        boolean hasContainerItem = false;
        try {
            //Note: We include the hasContainerItem check in the try catch, as if a mod is handling tags incorrectly
            // there is a chance their hasContainerItem is checking something about tags, and
            hasContainerItem = item.hasCraftingRemainingItem(stack);
            if (hasContainerItem) {
                //If this item has a container for the stack, we remove the cost of the container itself
                ingredientMap.addIngredient(NSSItem.createItem(item.getCraftingRemainingItem(stack)), -1);
            }
        } catch (Exception e) {
            ResourceLocation itemName = ItemHelper.getName(item);
            if (hasContainerItem) {
                if (isTagException(e)) {
                    AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} reported that it has a container item, "
                            + "but errors when trying to get the container item due to not properly deserializing and handling tags. "
                            + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
                } else {
                    AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} reported that it has a container item, "
                            + "but errors when trying to get the container item based on the stack in the recipe. "
                            + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
                }
            } else if (isTagException(e)) {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} crashed when checking if the stack has a container item, "
                                + "due to not properly deserializing and handling tags. Please report this to {}.", recipeID, itemName,
                        itemName.getNamespace(), e);
            } else {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} crashed when checking if the stack in the recipe has a container item. "
                        + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
            }
            //If something failed because the recipe errored, return that we did handle it so that we don't try to handle it later
            // as there is a 99% chance it will just fail again anyways
            return true;
        }
        ingredientMap.addIngredient(NSSItem.createItem(stack), 1);
        return false;
    }

    private boolean isTagException(Exception e) {
        return e instanceof IllegalStateException && e.getMessage().matches("Tag \\S*:\\S* used before it was bound");
    }

    @Nullable
    private Collection<Ingredient> getIngredientsChecked(R recipe) {
        try {
            return getIngredients(recipe);
        } catch (Exception e) {
            ResourceLocation recipeID = recipe.getId();
            if (isTagException(e)) {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Failed to get ingredients due to the recipe not properly deserializing and handling tags. "
                        + "Please report this to {}.", recipeID, recipeID.getNamespace(), e);
            } else {
                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Failed to get ingredients. Please report this to {}.", recipeID, recipeID.getNamespace(), e);
            }
        }
        return null;
    }

    //Allow overwriting the ingredients list because Smithing recipes don't override it themselves
    protected Collection<Ingredient> getIngredients(R recipe) {
        return recipe.getIngredients();
    }

    protected void bundleInputWithFluids(IngredientMap<NormalizedSimpleStack> ingredientMap, List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap , INSSFakeGroupManager fakeGroupManager, List<FluidIngredient> fluidIngredients){
        for (FluidIngredient fluidIngredient : fluidIngredients) {
            final int amount = fluidIngredient.getRequiredAmount();
            List<FluidStack> matches = fluidIngredient.getMatchingFluidStacks();
            if (matches.isEmpty()) {
                continue;
            }

            if (matches.size() == 1) {
                ingredientMap.addIngredient(NSSFluid.createFluid(matches.get(0)), amount);
            } else {
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                List<FluidStack> stacks = new ArrayList<>();

                for (FluidStack match : matches) {
                    //Validate it is not an empty stack in case mods do weird things in custom ingredients
                    if (!match.isEmpty()) {
                        rawNSSMatches.add(NSSFluid.createFluid(match));
                        stacks.add(match);
                    }
                }

                int count = stacks.size();
                if (count == 1) {
                    ingredientMap.addIngredient(NSSFluid.createFluid(stacks.get(0)), amount);
                } else if (count > 1) {
                    //Handle this ingredient as the representation of all the stacks it supports
                    Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                    NormalizedSimpleStack dummy = group.getA();
                    ingredientMap.addIngredient(dummy, Math.max(amount, 1));
                    if (group.getB()) {
                        //Only lookup the matching stacks for the group with conversion if we don't already have
                        // a group created for this dummy ingredient
                        // Note: We soft ignore cases where it fails/there are no matching group ingredients
                        // as then our fake ingredient will never actually have an emc value assigned with it
                        // so the recipe won't either
                        List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                        for (FluidStack stack : stacks) {
                            IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                            groupIngredientMap.addIngredient(NSSFluid.createFluid(stack), 1);
                            groupIngredientMaps.add(groupIngredientMap);
                        }
                        fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                    }
                }
            }
        }
    }

    /**
     * Interface to make for a cleaner API than using a {@link java.util.function.Function} when creating groupings of {@link NormalizedSimpleStack}s.
     */
    public interface INSSFakeGroupManager {

        /**
         * Gets or creates a singular {@link NormalizedSimpleStack} representing the grouping or "ingredient" of the given stacks. Additionally a boolean is returned
         * specifying if it was created or already existed. {@code true} for if it was created.
         *
         * @param stacks Individual stacks to represent as a single "combined" stack.
         *
         * @apiNote If the combined representation had to be created ({@code true} for the second element of the {@link Tuple}), then conversions from the individual elements
         * to the returned stack <strong>MUST</strong> be added.
         */
        Tuple<NormalizedSimpleStack, Boolean> getOrCreateFakeGroup(Set<NormalizedSimpleStack> stacks);
    }
}
