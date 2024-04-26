package com.robocraft999.amazingtrading.kubejs;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.IRPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSSerializer;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

@RPMapper(priority = -1000)
public class KubeJSRPMapperAfter implements IRPMapper<NormalizedSimpleStack, Long> {
    @RPMapper.Instance
    public static final KubeJSRPMapperAfter INSTANCE = new KubeJSRPMapperAfter();

    @Override
    public String getName() {
        return "KubeJSAmazingTrading";
    }

    @Override
    public String getDescription() {
        return "Allows setting rp values through KubeJS";
    }

    public Map<String, Long> items = new HashMap<>();
    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        for (var entry : items.entrySet()) {
            mapper.setValueBefore(NSSSerializer.INSTANCE.deserialize(entry.getKey()), entry.getValue());
        }
    }
}
