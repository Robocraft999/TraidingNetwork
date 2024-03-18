package com.robocraft999.traidingnetwork.api.capabilities.impl;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IShopNetworkSync;
import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.net.packets.shop.SyncClientSettingsPKT;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopSettingsProviderImpl {
    public static IShopNetworkSync getDefault() {
        return new ShopSettingsProviderImpl.DefaultImpl(null);
    }

    public static class DefaultImpl implements IShopNetworkSync, INBTSerializable<CompoundTag> {
        @Nullable
        private final Player player;
        private boolean downwards;
        private EnumSortType sort = EnumSortType.NAME;
        private boolean autoFocus = true;

        private DefaultImpl(@Nullable Player player) {
            this.player = player;
        }

        @Override
        public void sync(@NotNull ServerPlayer player) {
            PacketHandler.sendTo(new SyncClientSettingsPKT(serializeNBT()), player);
        }

        @Override
        public boolean isDownwards() {
            return downwards;
        }

        @Override
        public void setDownwards(boolean downwards) {
            this.downwards = downwards;
        }

        @Override
        public EnumSortType getSort() {
            return sort;
        }

        @Override
        public void setSort(EnumSortType sort) {
            this.sort = sort;
        }

        @Override
        public void setAutoFocus(boolean autoFocus) {
            this.autoFocus = autoFocus;
        }

        @Override
        public boolean getAutoFocus() {
            return autoFocus;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compound = new CompoundTag();
            compound.putBoolean("dir", isDownwards());
            compound.putInt("sort", getSort().ordinal());
            compound.putBoolean("autoFocus", getAutoFocus());
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            setAutoFocus(compound.getBoolean("autoFocus"));
            setDownwards(compound.getBoolean("dir"));
            setSort(EnumSortType.values()[compound.getInt("sort")]);
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation(TraidingNetwork.MODID, "shopsettingsprovider");
        private final NonNullSupplier<IShopNetworkSync> supplier;
        private LazyOptional<IShopNetworkSync> cachedCapability;

        public Provider(Player player){
            IShopNetworkSync cap = new ShopSettingsProviderImpl.DefaultImpl(player);
            supplier = () -> cap;
        }

        @NotNull
        public <T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> capability, @Nullable Direction side) {
            if (cachedCapability == null || !cachedCapability.isPresent()) {
                //If the capability has not been retrieved yet or it is not valid then recreate it
                cachedCapability = LazyOptional.of(supplier);
            }
            return cachedCapability.cast();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            if (capability == TNCapabilities.SHOP_SETTINGS_CAPABILITY){
                return getCapabilityUnchecked(capability, direction);
            }
            return LazyOptional.empty();
        }

        public void invalidateAll() {
            if (cachedCapability != null && cachedCapability.isPresent()) {
                cachedCapability.invalidate();
                cachedCapability = null;
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            return supplier.get().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            supplier.get().deserializeNBT(compoundTag);
        }
    }

    private ShopSettingsProviderImpl(){}
}
