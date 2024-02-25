package com.robocraft999.traidingnetwork.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IResourceItemProvider extends INBTSerializable<CompoundTag> {
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
    IItemHandler getSlotsHandler();

    void sync(@NotNull ServerPlayer player);

    void syncSlots(@NotNull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets);

    void receiveSlots(Map<Integer, ItemStack> changes);
}
