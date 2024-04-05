package com.robocraft999.amazingtrading.resourcepoints.mapper;

import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.coiling.CoilingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HammeringRecipe;
import com.robocraft999.amazingtrading.api.mapper.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;

public class CreateVintageImprovementsMapper {
    @RecipeTypeMapper(requiredMods = "vintageimprovements", priority = -1)
    public static class CreateVICoilingMapper extends CreateMapper.CreateProcessingRecipeMapper<CoilingRecipe> {

        @Override
        public String getName() {
            return "CoilingMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == VintageRecipes.COILING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = "vintageimprovements", priority = -1)
    public static class CreateVIHammeringMapper extends CreateMapper.CreateProcessingRecipeMapper<HammeringRecipe> {

        @Override
        public String getName() {
            return "HammeringMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == VintageRecipes.HAMMERING.getType();
        }
    }
}
