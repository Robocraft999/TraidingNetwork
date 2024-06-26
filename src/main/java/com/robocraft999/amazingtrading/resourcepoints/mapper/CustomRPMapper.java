package com.robocraft999.amazingtrading.resourcepoints.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSTag;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

@RPMapper
public class CustomRPMapper implements IRPMapper<NormalizedSimpleStack, Long> {
    @Override
    public String getName() {
        return "CustomRPMapper";
    }

    @Override
    public String getDescription() {
        return "Uses the `custom_rp.json` File to add RP values.";
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        for (CustomRPParser.CustomRPEntry entry : CustomRPParser.currentEntries.entries) {
            AmazingTrading.LOGGER.debug("Adding custom RP value for {}: {}", entry.item, entry.rp);
            mapper.setValueBefore(entry.item, entry.rp);
            if (entry.item instanceof NSSTag nssTag) {
                //Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual RP values
                nssTag.forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, entry.rp));
            }
        }
    }
}
