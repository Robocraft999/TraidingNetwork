package com.robocraft999.traidingnetwork.blockentity;

import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointStorage;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class ShopBlockEntity extends BlockEntity implements IResourcePointStorage{
    private LazyOptional<IResourcePointStorage> rpStorageCapability;

    public ShopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(TNBlockEntities.SHOP.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == TNCapabilities.RESOURCE_POINT_STORAGE_CAPABILITY){
            if (rpStorageCapability == null || !rpStorageCapability.isPresent()){
                rpStorageCapability = LazyOptional.of(() -> this);
            }
            return rpStorageCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (rpStorageCapability != null && rpStorageCapability.isPresent()){
            rpStorageCapability.invalidate();
            rpStorageCapability = null;
        }
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getStoredPoints() {
        return 0;
    }

    @Override
    public @Range(from = 1, to = Long.MAX_VALUE) long getMaximumPoints() {
        return 0;
    }

    @Override
    public long extractPoints(long toExtract, RPAction action) {
        return 0;
    }

    @Override
    public long insertPoints(long toAccept, RPAction action) {
        return 0;
    }
}
