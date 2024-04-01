package com.robocraft999.amazingtrading.resourcepoints.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSItem;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Collections;
import java.util.Map;

@RPMapper
public class WeatheredCopperMapper implements IRPMapper<NormalizedSimpleStack, Long> {
    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
                            RegistryAccess registryAccess, ResourceManager resourceManager) {
        int recipeCount = 0;
        for (Map.Entry<Block, Block> entry : WeatheringCopper.NEXT_BY_BLOCK.get().entrySet()) {
            //Add conversions both directions due to scraping
            NSSItem unweathered = NSSItem.createItem(entry.getKey());
            NSSItem weathered = NSSItem.createItem(entry.getValue());
            mapper.addConversion(1, weathered, Collections.singleton(unweathered));
            mapper.addConversion(1, unweathered, Collections.singleton(weathered));
            recipeCount += 2;
        }
        AmazingTrading.LOGGER.debug("WeatheredCopperMapper Statistics:");
        AmazingTrading.LOGGER.debug("Found {} Weathered Copper Conversions", recipeCount);
    }

    @Override
    public String getName() {
        return "WeatheredCopperMapper";
    }

    @Override
    public String getDescription() {
        return "Add Conversions for all weathered copper variants";
    }
}
