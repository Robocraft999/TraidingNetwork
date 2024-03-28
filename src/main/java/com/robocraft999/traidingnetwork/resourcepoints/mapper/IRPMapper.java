package com.robocraft999.traidingnetwork.resourcepoints.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.collector.IMappingCollector;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Interface for Classes that want to make Contributions to the RP Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the RP Value
 */
public interface IRPMapper<T, V extends Comparable<V>> {

    /**
     * A unique Name for the {@link IRPMapper}. This is used to identify the {@link IRPMapper} in the Configuration.
     *
     * @return A unique Name
     */
    String getName();

    /**
     * A Description, that will be included as a Comment in the Configuration File
     *
     * @return A <b>short</b> description
     */
    String getDescription();

    /**
     * This method is used to determine the default for enabling/disabling this {@link IRPMapper}. If this returns {@code false} {@link #addMappings} will not be
     * called.
     *
     * @return {@code true} if you want {@link #addMappings} to be called, {@code false} otherwise.
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * The method that allows the {@link IRPMapper} to contribute to the RP Mapping. Use the methods provided by the {@link IMappingCollector}. <br/> Use the config
     * object to generate a useful Configuration for your {@link IRPMapper}. <br/> The Configuration Object will be a {@link
     * com.electronwill.nightconfig.core.file.CommentedFileConfig} representing the top-level mapping.cfg file. Please use properly prefixed config keys and do not
     * clobber those not belonging to your mapper
     */
    void addMappings(IMappingCollector<T, V> mapper, CommentedFileConfig config, ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager);
}
