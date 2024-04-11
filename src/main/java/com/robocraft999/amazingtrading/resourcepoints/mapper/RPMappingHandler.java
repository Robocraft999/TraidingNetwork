package com.robocraft999.amazingtrading.resourcepoints.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.Config;
import com.robocraft999.amazingtrading.api.ItemInfo;
import com.robocraft999.amazingtrading.api.capabilities.impl.ResourceItemProviderImpl;
import com.robocraft999.amazingtrading.net.SyncResourcePointPKT.ResourcePointPKTInfo;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import com.robocraft999.amazingtrading.resourcepoints.PregeneratedRP;
import com.robocraft999.amazingtrading.resourcepoints.mapper.arithmetic.HiddenBigFractionArithmetic;
import com.robocraft999.amazingtrading.resourcepoints.mapper.arithmetic.IValueArithmetic;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.DumpToFileCollector;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.IExtendedMappingCollector;
import com.robocraft999.amazingtrading.resourcepoints.mapper.collector.LongToBigFractionCollector;
import com.robocraft999.amazingtrading.resourcepoints.mapper.generator.BigFractionToLongGenerator;
import com.robocraft999.amazingtrading.resourcepoints.mapper.generator.IValueGenerator;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSItem;
import com.robocraft999.amazingtrading.resourcepoints.nss.NormalizedSimpleStack;
import com.robocraft999.amazingtrading.utils.AnnotationHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.math3.fraction.BigFraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RPMappingHandler {
    private static final List<IRPMapper<NormalizedSimpleStack, Long>> mappers = new ArrayList<>();
    private static final Map<ItemInfo, Long> points = new HashMap<>();
    private static int loadIndex = -1;

    public static void loadMappers() {
        //If we don't have any mappers loaded try to load them
        if (mappers.isEmpty()) {
            //Add all the RP mappers we have encountered
            mappers.addAll(AnnotationHelper.getRPMappers());
            //Manually register the Tag Mapper to ensure that it is registered last so that it can "fix" all the tags used in any of the other mappers
            // This also has the side effect to make sure that we can use RP_MAPPERS.isEmpty to check if we have attempted to initialize our cache yet
            mappers.add(new TagMapper());
        }
    }

    public static <T> T getOrSetDefault(CommentedFileConfig config, String key, String comment, T defaultValue) {
        T val = config.get(key);
        if (val == null) {
            val = defaultValue;
            config.set(key, val);
            config.setComment(key, comment);
        }
        return val;
    }

    public static void map(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        //Start by clearing the cached map so if values are removed say by setting RP to zero then we respect the change
        clearRpMap();
        SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
        IValueGenerator<NormalizedSimpleStack, Long> valueGenerator = new BigFractionToLongGenerator<>(mapper);
        IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);

        Path path = Config.CONFIG_DIR.resolve("mapping.toml");
        try {
            if (path.toFile().createNewFile()) {
                AmazingTrading.LOGGER.debug("Created mapping.toml");
            }
        } catch (IOException ex) {
            AmazingTrading.LOGGER.error("Couldn't create mapping.toml", ex);
        }

        CommentedFileConfig config = CommentedFileConfig.builder(path).build();
        config.load();

        boolean dumpToFile = getOrSetDefault(config, "general.dumpEverythingToFile", "Want to take a look at the internals of RP Calculation? Enable this to write all the conversions and setValue-Commands to config/Traidingnetwork/mappingdump.json", false);
        boolean shouldUsePregenerated = getOrSetDefault(config, "general.pregenerate", "When the next RP mapping occurs write the results to config/Traidingnetwork/pregenerated_rp.json and only ever run the mapping again" +
                " when that file does not exist, this setting is set to false, or an error occurred parsing that file.", false);
        boolean logFoundExploits = getOrSetDefault(config, "general.logRPExploits", "Log known RP Exploits. This can not and will not find all possible exploits. " +
                "This will only find exploits that result in fixed/custom rp values that the algorithm did not overwrite. " +
                "Exploits that derive from conversions that are unknown to Traidingnetwork will not be found.", true);

        if (dumpToFile) {
            mappingCollector = new DumpToFileCollector<>(Config.CONFIG_DIR.resolve("mappingdump.json").toFile(), mappingCollector);
        }

        File pregeneratedRpFile = Paths.get("config", AmazingTrading.NAME, "pregenerated_rp.json").toFile();
        Map<NormalizedSimpleStack, Long> graphMapperValues;
        if (shouldUsePregenerated && pregeneratedRpFile.canRead() && PregeneratedRP.tryRead(pregeneratedRpFile, graphMapperValues = new HashMap<>())) {
            AmazingTrading.LOGGER.info("Loaded {} values from pregenerated RP File", graphMapperValues.size());
        } else {
            SimpleGraphMapper.setLogFoundExploits(logFoundExploits);

            AmazingTrading.LOGGER.debug("Starting to collect Mappings...");
            for (IRPMapper<NormalizedSimpleStack, Long> rpMapper : mappers) {
                try {
                    if (getOrSetDefault(config, "enabledMappers." + rpMapper.getName(), rpMapper.getDescription(), rpMapper.isAvailable())) {
                        DumpToFileCollector.currentGroupName = rpMapper.getName();
                        rpMapper.addMappings(mappingCollector, config, serverResources, registryAccess, resourceManager);
                        AmazingTrading.LOGGER.info("Collected Mappings from " + rpMapper.getClass().getName());
                    }
                } catch (Exception e) {
                    AmazingTrading.LOGGER.error(LogUtils.FATAL_MARKER, "Exception during Mapping Collection from Mapper {}. PLEASE REPORT THIS! RP VALUES MIGHT BE INCONSISTENT!",
                            rpMapper.getClass().getName(), e);
                }
            }
            DumpToFileCollector.currentGroupName = "NSSHelper";

            AmazingTrading.LOGGER.debug("Mapping Collection finished");
            mappingCollector.finishCollection();

            AmazingTrading.LOGGER.debug("Starting to generate Values:");

            config.save();
            config.close();

            graphMapperValues = valueGenerator.generateValues();
            AmazingTrading.LOGGER.debug("Generated Values...");
            //AmazingTrading.LOGGER.debug("raw values: " + graphMapperValues.size() + " :" + graphMapperValues);

            filterRPMap(graphMapperValues);

            if (shouldUsePregenerated) {
                //Should have used pregenerated, but the file was not read => regenerate.
                try {
                    PregeneratedRP.write(pregeneratedRpFile, graphMapperValues);
                    AmazingTrading.LOGGER.debug("Wrote Pregen-file!");
                } catch (IOException e) {
                    AmazingTrading.LOGGER.error("Failed to write Pregen-file", e);
                }
            }
        }

        //AmazingTrading.LOGGER.debug("mappings: " + graphMapperValues.size() + " :" + graphMapperValues);

        for (Map.Entry<NormalizedSimpleStack, Long> entry : graphMapperValues.entrySet()) {
            NSSItem normStackItem = (NSSItem) entry.getKey();
            ItemInfo obj = ItemInfo.fromNSS(normStackItem);
            if (obj != null) {
                points.put(obj, entry.getValue());
            } else {
                AmazingTrading.LOGGER.warn("Could not add RP value for {}, item does not exist!", normStackItem.getResourceLocation());
            }
        }

        fireRpRemapEvent();
    }

    private static void fireRpRemapEvent() {
        //Start by doing our implementations
        //FuelMapper.loadMap();
        loadIndex++;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.getCapability(ATCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(itemProvider -> {
                    if (itemProvider instanceof ResourceItemProviderImpl.DefaultImpl impl/* && impl.pruneStaleKnowledge()*/) {
                        itemProvider.sync();
                    }/* else if (player.containerMenu instanceof TransmutationContainer) {
                        //If knowledge didn't get trimmed due to pruning, tell clients that have the transmutation gui open
                        // that they should update targets anyway, as it is possible RP values changed and the order things
                        // are drawn needs to be changed
                        PacketHandler.sendTo(new UpdateTransmutationTargetsPkt(), player);
                    }*/
                });
            }
        }
        //MinecraftForge.EVENT_BUS.post(new RPRemapEvent());
    }

    public static int getLoadIndex() {
        return loadIndex;
    }

    private static void filterRPMap(Map<NormalizedSimpleStack, Long> map) {
        map.entrySet().removeIf(e -> !(e.getKey() instanceof NSSItem nssItem) || nssItem.representsTag() || e.getValue() <= 0);
    }

    public static int getRpMapSize() {
        return points.size();
    }

    public static boolean hasRpValue(@NotNull ItemInfo info) {
        return points.containsKey(info);
    }

    /**
     * Gets the stored rp value or zero if there is no entry in the map for the given value.
     */
    @Range(from = 0, to = Long.MAX_VALUE)
    public static long getStoredRpValue(@NotNull ItemInfo info) {
        return points.getOrDefault(info, 0L);
    }

    public static void clearRpMap() {
        points.clear();
    }

    /**
     * Returns a modifiable set of all the mapped {@link ItemInfo}
     */
    public static Set<ItemInfo> getMappedItems() {
        return new HashSet<>(points.keySet());
    }


    public static void fromPacket(ResourcePointPKTInfo[] data) {
        points.clear();
        for (ResourcePointPKTInfo info : data) {
            points.put(ItemInfo.fromItem(info.item(), info.nbt()), info.points());
        }
    }
    public static ResourcePointPKTInfo[] createPacketData() {
        ResourcePointPKTInfo[] ret = new ResourcePointPKTInfo[points.size()];
        int i = 0;
        for (Map.Entry<ItemInfo, Long> entry : points.entrySet()) {
            ItemInfo info = entry.getKey();
            ret[i] = new ResourcePointPKTInfo(info.getItem(), info.getNBT(), entry.getValue());
            i++;
        }
        return ret;
    }
}
