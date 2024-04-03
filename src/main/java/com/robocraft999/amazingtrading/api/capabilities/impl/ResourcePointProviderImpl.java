package com.robocraft999.amazingtrading.api.capabilities.impl;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.capabilities.IResourcePointProvider;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.net.SyncProviderPKT;
import com.robocraft999.amazingtrading.net.SyncProviderResourcePointPKT;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
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

import java.math.BigInteger;

public class ResourcePointProviderImpl {
    public static IResourcePointProvider getDefault() {
        return new DefaultImpl(null);
    }

    public static class DefaultImpl implements IResourcePointProvider {
        @Nullable
        private final Player player;
        private BigInteger points = BigInteger.ZERO;

        private boolean secretEnabled = false;

        private DefaultImpl(@Nullable Player player) {
            this.player = player;
        }

        @Override
        public BigInteger getPoints() {
            return points;
        }

        @Override
        public void setPoints(BigInteger points) {
            this.points = points;
        }

        @Override
        public void syncPoints(@NotNull ServerPlayer player) {
            PacketHandler.sendTo(new SyncProviderResourcePointPKT(getPoints()), player);
        }

        @Override
        public void sync(@NotNull ServerPlayer player) {
            PacketHandler.sendTo(new SyncProviderPKT(serializeNBT()), player);
        }

        @Override
        public boolean isSecretEnabled() {
            return secretEnabled;
        }

        @Override
        public void enableSecret() {
            secretEnabled = true;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag properties = new CompoundTag();
            properties.putString("resourcepoints", points.toString());
            properties.putBoolean("secretEnabled", secretEnabled);
            return properties;
        }

        @Override
        public void deserializeNBT(CompoundTag properties) {
            String resourcePoints = properties.getString("resourcepoints");
            points = resourcePoints.isEmpty() ? BigInteger.ZERO : new BigInteger(resourcePoints);
            secretEnabled = properties.getBoolean("secretEnabled");
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation(AmazingTrading.MODID, "resourcepointprovider");
        private final NonNullSupplier<IResourcePointProvider> supplier;
        private LazyOptional<IResourcePointProvider> cachedCapability;

        public Provider(Player player){
            IResourcePointProvider cap = new DefaultImpl(player);
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
            if (capability == ATCapabilities.RESOURCE_POINT_CAPABILITY){
                return getCapabilityUnchecked(capability, direction);
                //return LazyOptional.of(() -> TNCapabilities.RESOURCE_POINT_CAPABILITY).cast();
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

    private ResourcePointProviderImpl(){}
}
