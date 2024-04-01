package com.robocraft999.amazingtrading.api.capabilities.impl;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.api.capabilities.IResourceItemProvider;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.net.SyncItemProviderPKT;
import com.robocraft999.amazingtrading.net.SyncSlotsPKT;
import com.robocraft999.amazingtrading.registry.TNCapabilities;
import com.robocraft999.amazingtrading.resourcepoints.RItemStackHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceItemProviderImpl {

    public static IResourceItemProvider getDefault() {
        return new ResourceItemProviderImpl.DefaultImpl(null);
    }

    public static class DefaultImpl implements IResourceItemProvider{

        @Nullable
        private final Level level;
        private final RItemStackHandler slots = new RItemStackHandler();

        private DefaultImpl(@Nullable Level level) {
            this.level = level;
        }

        @Override
        public IItemHandler getSlotsHandler() {
            return this.slots;
        }

        @Override
        public void sync() {
            PacketHandler.sendToAll(new SyncItemProviderPKT(serializeNBT()));
        }

        @Override
        public void syncSlots(@NotNull ServerPlayer player, List<Integer> slotsChanged, TargetUpdateType updateTargets) {
            if (!slotsChanged.isEmpty()) {
                int slots = this.slots.getSlots();
                Map<Integer, ItemStack> stacksToSync = new HashMap<>();
                for (int slot : slotsChanged) {
                    if (slot >= 0 && slot < slots) {
                        //Validate the slot is a valid index
                        stacksToSync.put(slot, this.slots.getStackInSlot(slot));
                    }
                }
                if (!stacksToSync.isEmpty()) {
                    //Validate it is not empty in case we were fed bad indices
                    PacketHandler.sendTo(new SyncSlotsPKT(stacksToSync, updateTargets), player);
                }
            }
        }

        @Override
        public void receiveSlots(Map<Integer, ItemStack> changes) {
            int slots = this.slots.getSlots();
            for (Map.Entry<Integer, ItemStack> entry : changes.entrySet()) {
                int slot = entry.getKey();
                if (slot >= 0 && slot < slots) {
                    //Validate the slot is a valid index
                    this.slots.setStackInSlot(slot, entry.getValue());
                }
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("slots", slots.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            slots.deserializeNBT(nbt.getCompound("slots"));
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation NAME = new ResourceLocation(TraidingNetwork.MODID, "rpslots");
        private final NonNullSupplier<IResourceItemProvider> supplier;
        private LazyOptional<IResourceItemProvider> cachedCapability;

        public Provider(Level level){
            IResourceItemProvider cap = new DefaultImpl(level);
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
            if (capability == TNCapabilities.RESOURCE_ITEM_CAPABILITY){
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
}
