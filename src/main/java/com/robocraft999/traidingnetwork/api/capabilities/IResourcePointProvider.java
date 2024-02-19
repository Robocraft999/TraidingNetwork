package com.robocraft999.traidingnetwork.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface IResourcePointProvider extends INBTSerializable<CompoundTag> {

    enum TargetUpdateType {
        /**
         * Don't update targets.
         */
        NONE,
        /**
         * Only update if "needed", the points value is below the highest item.
         */
        IF_NEEDED,
        /**
         * Update targets.
         */
        ALL
    }

    BigInteger getPoints();

    void setPoints(BigInteger points);

    void syncPoints(@NotNull ServerPlayer player);

    void sync(@NotNull ServerPlayer player);

    IItemHandler getInputAndLocks();

    /**
     * Syncs the inputs and locks stored in this provider to the given player.
     *
     * @param player        The player to sync to.
     * @param slotsChanged  The indices of the slots that need to be synced (may be empty, in which case nothing should happen).
     * @param updateTargets How the targets should be updated on the client.
     */
    void syncInputAndLocks(@NotNull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets);

    /**
     * @param changes Slot index to stack for the changes that occurred.
     *
     * @apiNote Should only really be used on the client for purposes of receiving/handling {@link #syncInputAndLocks(ServerPlayer, List, TargetUpdateType)}
     */
    void receiveInputsAndLocks(Map<Integer, ItemStack> changes);
}
