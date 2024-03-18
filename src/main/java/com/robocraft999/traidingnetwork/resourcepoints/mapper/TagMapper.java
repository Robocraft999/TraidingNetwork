package com.robocraft999.traidingnetwork.resourcepoints.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.traidingnetwork.resourcepoints.nss.NSSItem;
import com.robocraft999.traidingnetwork.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collections;

public class TagMapper implements IRPMapper<NormalizedSimpleStack, Long> {
    @Override
    public String getName() {
        return "TagMapper";
    }

    @Override
    public String getDescription() {
        return "Adds back and forth conversions of objects and their Tag variant. (RP values assigned to tags will not behave properly if this mapper is disabled)";
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, ReloadableServerResources serverResources,
                            RegistryAccess registryAccess, ResourceManager resourceManager) {
        NSSItem.getAllCreatedTags().forEach(stack -> stack.forEachElement(normalizedSimpleStack -> {
            //Tag -> element
            mapper.addConversion(1, stack, Collections.singletonList(normalizedSimpleStack));
            //Element -> tag
            mapper.addConversion(1, normalizedSimpleStack, Collections.singletonList(stack));
        }));
    }
}
