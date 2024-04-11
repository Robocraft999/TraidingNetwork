package com.robocraft999.amazingtrading.resourcepoints.conversion;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.mapper.RPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.IRPMapper;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSFake;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSSerializer;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSTag;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RPMapper
public class CustomConversionMapper implements IRPMapper<NormalizedSimpleStack, Long> {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CustomConversion.class, new CustomConversionDeserializer())
            .registerTypeAdapter(FixedValues.class, new FixedValuesDeserializer())
            .registerTypeAdapter(NormalizedSimpleStack.class, NSSSerializer.INSTANCE)
            .setPrettyPrinting()
            .create();

    @Override
    public String getName() {
        return "CustomConversionMapper";
    }

    @Override
    public String getDescription() {
        return "Loads json files within datapacks (data/<domain>/tn_custom_conversions/*.json) to add values and conversions";
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
                            RegistryAccess registryAccess, ResourceManager resourceManager) {
        Map<ResourceLocation, CustomConversionFile> files = load(resourceManager);
        for (CustomConversionFile file : files.values()) {
            addMappingsFromFile(file, mapper);
        }
    }

    private static Map<ResourceLocation, CustomConversionFile> load(ResourceManager resourceManager) {
        Map<ResourceLocation, CustomConversionFile> loading = new HashMap<>();

        String folder = "tn_custom_conversions";
        String extension = ".json";
        int folderLength = folder.length();
        int extensionLength = extension.length();

        // Find all data/<domain>/pe_custom_conversions/foo/bar.json
        for (Map.Entry<ResourceLocation, List<Resource>> entry : resourceManager.listResourceStacks(folder, n -> n.getPath().endsWith(extension)).entrySet()) {
            // <domain>:foo/bar
            ResourceLocation file = entry.getKey();
            ResourceLocation conversionId = new ResourceLocation(file.getNamespace(), file.getPath().substring(folderLength + 1, file.getPath().length() - extensionLength));

            AmazingTrading.LOGGER.info("Considering file {}, ID {}", file, conversionId);
            NSSFake.setCurrentNamespace(conversionId.toString());

            // Iterate through all copies of this conversion, from lowest to highest priority datapack, merging the results together
            try {
                for (Resource resource : entry.getValue()) {
                    CustomConversionFile result;
                    try (Reader reader = resource.openAsReader()) {
                        result = parseJson(reader);
                    } catch (JsonParseException ex) {
                        AmazingTrading.LOGGER.error("Malformed JSON", ex);
                        continue;
                    }
                    loading.merge(conversionId, result, CustomConversionFile::merge);
                }
            } catch (IOException e) {
                AmazingTrading.LOGGER.error("Could not load resource {}", file, e);
            }
        }
        NSSFake.resetNamespace();
        return loading;
    }

    private static void addMappingsFromFile(CustomConversionFile file, IMappingCollector<NormalizedSimpleStack, Long> mapper) {
        for (Map.Entry<String, ConversionGroup> entry : file.groups.entrySet()) {
            AmazingTrading.LOGGER.debug("Adding conversions from group '{}' with comment '{}'", entry.getKey(), entry.getValue().comment);
            for (CustomConversion conversion : entry.getValue().conversions) {
                mapper.addConversion(conversion.count, conversion.output, conversion.ingredients);
            }
        }

        for (Map.Entry<NormalizedSimpleStack, Long> entry : file.values.setValueBefore.entrySet()) {
            NormalizedSimpleStack something = entry.getKey();
            mapper.setValueBefore(something, entry.getValue());
            if (something instanceof NSSTag nssTag) {
                //Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual RP values
                nssTag.forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, entry.getValue()));
            }
        }

        for (Map.Entry<NormalizedSimpleStack, Long> entry : file.values.setValueAfter.entrySet()) {
            NormalizedSimpleStack something = entry.getKey();
            mapper.setValueAfter(something, entry.getValue());
            if (something instanceof NSSTag nssTag) {
                //Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual RP values
                nssTag.forEachElement(normalizedSimpleStack -> mapper.setValueAfter(normalizedSimpleStack, entry.getValue()));
            }
        }

        for (CustomConversion conversion : file.values.conversion) {
            NormalizedSimpleStack out = conversion.output;
            if (conversion.propagateTags && out instanceof NSSTag nssTag) {
                nssTag.forEachElement(normalizedSimpleStack -> mapper.setValueFromConversion(conversion.count, normalizedSimpleStack, conversion.ingredients));
            }
            mapper.setValueFromConversion(conversion.count, out, conversion.ingredients);
            //AmazingTrading.LOGGER.debug("CCM: Value from Conversion of {}*{} ->{}", conversion.count, conversion.ingredients, out);
        }
    }

    public static CustomConversionFile parseJson(Reader json) {
        return GSON.fromJson(new BufferedReader(json), CustomConversionFile.class);
    }
}
