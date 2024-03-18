package com.robocraft999.traidingnetwork.api.capabilities;

import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public interface IShopNetworkSync extends INBTSerializable<CompoundTag> {
    boolean isDownwards();

    void setDownwards(boolean downwards);

    EnumSortType getSort();

    void setSort(EnumSortType sort);

    void setAutoFocus(boolean autoFocus);

    void sync(@NotNull ServerPlayer player);

    boolean getAutoFocus();
}
