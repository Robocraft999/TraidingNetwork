package com.robocraft999.amazingtrading.resourcepoints.mapper.recipe;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.IRPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.RPMappingHandler;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSFake;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import com.robocraft999.amazingtrading.utils.AnnotationHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@RPMapper
public class CraftingMapper implements IRPMapper<NormalizedSimpleStack, Long> {

    //Note: None of our defaults just directly support all recipe types, as mods may extend it for "random" things and have more input types required than just items
    // We also do this via annotations to allow for broader support for looping specific recipes and handling them
    private static final List<IRecipeTypeMapper> recipeMappers = new ArrayList<>();

    public static void loadMappers() {
        if (recipeMappers.isEmpty()) {
            recipeMappers.addAll(AnnotationHelper.getRecipeTypeMappers());
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, ReloadableServerResources serverResources,
                            RegistryAccess registryAccess, ResourceManager resourceManager) {
        NSSFake.setCurrentNamespace("craftingMapper");
        Map<ResourceLocation, RecipeCountInfo> recipeCount = new HashMap<>();
        Set<ResourceLocation> canNotMap = new HashSet<>();
        RecipeManager recipeManager = serverResources.getRecipeManager();
        //Make a new fake group manager here instead of across the entire mapper so that we can reclaim the memory when we are done with this method
        NSSFakeGroupManager fakeGroupManager = new NSSFakeGroupManager();
        for (Map.Entry<ResourceKey<RecipeType<?>>, RecipeType<?>> entry : ForgeRegistries.RECIPE_TYPES.getEntries()) {
            ResourceLocation typeRegistryName = entry.getKey().location();
            RecipeType<?> recipeType = entry.getValue();
            boolean wasHandled = false;
            List<? extends Recipe<?>> recipes = null;
            List<Recipe<?>> unhandled = new ArrayList<>();
            for (IRecipeTypeMapper recipeMapper : recipeMappers) {
                String configKey = getName() + "." + recipeMapper.getName() + ".enabled";
                if (RPMappingHandler.getOrSetDefault(config, configKey, recipeMapper.getDescription(), recipeMapper.isAvailable())) {
                    //If the sub mapper is enabled, use it
                    if (recipeMapper.canHandle(recipeType)) {
                        if (recipes == null) {
                            //If we haven't already retrieved the recipes, do so
                            //Note: The unchecked cast is needed as while the IDE doesn't have a warning without it
                            // it will not actually compile due to IRecipeType's generic only having to be of IRecipe<?>
                            // so no information is stored about the type of inventory for the recipe
                            recipes = recipeManager.getAllRecipesFor((RecipeType) recipeType);
                        }
                        int numHandled = 0;
                        for (Recipe<?> recipe : recipes) {
                            try {
                                if (recipeMapper.handleRecipe(mapper, recipe, registryAccess, fakeGroupManager)) {
                                    numHandled++;
                                } else {
                                    unhandled.add(recipe);
                                }
                            } catch (Exception e) {
                                AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "A fatal error occurred while trying to map the recipe: {}", recipe.getId());
                                throw e;
                            }
                        }
                        if (numHandled > 0 || recipes.isEmpty()) {
                            if (recipeCount.containsKey(typeRegistryName)) {
                                recipeCount.get(typeRegistryName).setUnhandled(unhandled);
                            } else {
                                recipeCount.put(typeRegistryName, new RecipeCountInfo(recipes.size(), unhandled));
                            }
                            wasHandled = true;
                            if (unhandled.isEmpty()) {
                                //If we have no more recipes that were unhandled break out of mapping this recipe type
                                break;
                            } else {
                                //Otherwise we replace our collection of known recipes with the unhandled ones and reset the list of unhandled recipes
                                recipes = unhandled;
                                unhandled = new ArrayList<>();
                            }
                        }
                        //If we didn't actually handle/map any recipes, continue looking
                    }
                }
            }
            if (!wasHandled) {
                //Note: We cannot just look at not unhandled is empty as then if none of the mappers even support the type
                // it will not be true. We also don't have any issues due to how we modify the unhandled
                canNotMap.add(typeRegistryName);
            }
        }
        AmazingTrading.LOGGER.debug("CraftingMapper Statistics:");
        for (Map.Entry<ResourceLocation, RecipeCountInfo> entry : recipeCount.entrySet()) {
            ResourceLocation typeRegistryName = entry.getKey();
            RecipeCountInfo countInfo = entry.getValue();
            int total = countInfo.getTotalRecipes();
            List<Recipe<?>> unhandled = countInfo.getUnhandled();
            AmazingTrading.LOGGER.debug("Found and handled {} of {} Recipes of Type {}", total - unhandled.size(), total, typeRegistryName);
            if (!unhandled.isEmpty()) {
                AmazingTrading.LOGGER.debug("Unhandled Recipes of Type {}:", typeRegistryName);
                for (Recipe<?> recipe : unhandled) {
                    AmazingTrading.LOGGER.debug("Name: {}, Recipe class: {}", recipe.getId(), recipe.getClass().getName());
                }
            }
        }
        for (ResourceLocation typeRegistryName : canNotMap) {
            AmazingTrading.LOGGER.debug("Could not map any Recipes of Type: {}", typeRegistryName);
        }
        NSSFake.resetNamespace();
    }

    @Override
    public String getName() {
        return "CraftingMapper";
    }

    @Override
    public String getDescription() {
        return "Add Conversions for Crafting Recipes gathered from net.minecraft.item.crafting.RecipeManager";
    }

    private static class RecipeCountInfo {

        private final int totalRecipes;
        private List<Recipe<?>> unhandled;

        private RecipeCountInfo(int totalRecipes, List<Recipe<?>> unhandled) {
            this.totalRecipes = totalRecipes;
            this.unhandled = unhandled;
        }

        public int getTotalRecipes() {
            return totalRecipes;
        }

        public void setUnhandled(List<Recipe<?>> unhandled) {
            this.unhandled = unhandled;
        }

        public List<Recipe<?>> getUnhandled() {
            return unhandled;
        }
    }

    private static class NSSFakeGroupManager implements BaseRecipeTypeMapper.INSSFakeGroupManager {

        private final Map<Set<NormalizedSimpleStack>, NormalizedSimpleStack> groups = new HashMap<>();
        private int fakeIndex;

        @Override
        public Tuple<NormalizedSimpleStack, Boolean> getOrCreateFakeGroup(Set<NormalizedSimpleStack> normalizedSimpleStacks) {
            NormalizedSimpleStack stack = groups.get(normalizedSimpleStacks);
            if (stack == null) {
                //Doesn't exist, create one with the next index add it as known and return
                // the group and the fact that we had to create a representation for it
                // Note: We use an incrementing index here as our crafting mapper sets a namespace
                // for NSSFake objects so we can safely use integers as the description and not
                // have to worry about intersecting fake stacks. We also for good measure specify in
                // the IRecipeTypeMapper java docs that if fake stacks are needed by an implementer
                // they should make sure to make the name more complex than just a simple integer to
                // ensure that they do not collide with stacks created by this method.
                stack = NSSFake.create(Integer.toString(fakeIndex++));
                //Copy the set into a new set to ensure that it can't be modified by changing
                // the set that was passed in
                groups.put(new HashSet<>(normalizedSimpleStacks), stack);
                return new Tuple<>(stack, true);
            }
            return new Tuple<>(stack, false);
        }
    }
}
