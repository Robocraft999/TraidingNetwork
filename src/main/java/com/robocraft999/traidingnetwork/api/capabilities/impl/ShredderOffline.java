package com.robocraft999.traidingnetwork.api.capabilities.impl;

import com.google.common.base.Preconditions;
import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.utils.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShredderOffline {

    private static final IResourcePointProvider NOT_FOUND_PROVIDER = immutableCopy(ResourcePointProviderImpl.getDefault());
    private static final Map<UUID, IResourcePointProvider> cachedResourcePointProviders = new HashMap<>();

    public static void clearAll() {
        cachedResourcePointProviders.clear();
    }

    public static void clear(UUID uuid){
        cachedResourcePointProviders.remove(uuid);
    }

    static IResourcePointProvider forPlayer(UUID playerUuid){
        if (!cachedResourcePointProviders.containsKey(playerUuid)){
            if (!cacheOfflineData(playerUuid)){
                cachedResourcePointProviders.put(playerUuid, NOT_FOUND_PROVIDER);
            }
        }
        return cachedResourcePointProviders.get(playerUuid);
    }

    private static boolean cacheOfflineData(UUID playerUUID){
        Preconditions.checkState(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER, "CRITICAL: Trying to read filesystem on client!!");
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        File playerData = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        if (playerData.exists()) {
            File player = new File(playerData, playerUUID.toString() + ".dat");
            if (player.exists() && player.isFile()) {
                try (FileInputStream in = new FileInputStream(player)) {
                    CompoundTag playerDat = NbtIo.readCompressed(in); // No need to create buffered stream, that call does it for us
                    CompoundTag knowledgeProvider = playerDat.getCompound("ForgeCaps").getCompound(ResourcePointProviderImpl.Provider.NAME.toString());

                    IResourcePointProvider provider = ResourcePointProviderImpl.getDefault();
                    provider.deserializeNBT(knowledgeProvider);
                    cachedResourcePointProviders.put(playerUUID, immutableCopy(provider));

                    TraidingNetwork.LOGGER.debug("Caching offline data for UUID: {}", playerUUID);
                    return true;
                } catch (IOException e) {
                    TraidingNetwork.LOGGER.warn("Failed to cache offline data for API calls for UUID: {}", playerUUID);
                }
            }
        }

        return false;
    }

    private static IResourcePointProvider immutableCopy(final IResourcePointProvider toCopy){
        return new IResourcePointProvider() {
            final IItemHandlerModifiable immutableInputLocks = ItemHelper.immutableCopy(toCopy.getInputAndLocks());
            @Override
            public BigInteger getPoints() {
                return toCopy.getPoints();
            }

            @Override
            public void setPoints(BigInteger points) {
            }

            @Override
            public void syncPoints(@NotNull ServerPlayer player) {
                toCopy.syncPoints(player);
            }

            @Override
            public void sync(@NotNull ServerPlayer player) {
                toCopy.sync(player);
            }

            @Override
            public IItemHandler getInputAndLocks() {
                return null;
            }

            @Override
            public void syncInputAndLocks(@NotNull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
                toCopy.syncInputAndLocks(player, slotsChanged, updateTargets);
            }

            @Override
            public void receiveInputsAndLocks(Map<Integer, ItemStack> changes) {
            }

            @Override
            public CompoundTag serializeNBT() {
                return toCopy.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundTag compoundTag) {
            }
        };
    }
}
