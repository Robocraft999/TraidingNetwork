package com.robocraft999.traidingnetwork.blockentity;

import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointStorage;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class RPBlockEntity extends BlockEntity implements IResourcePointStorage {

    private LazyOptional<IResourcePointStorage> rpStorageCapability;
    private long maximumRP;
    private long currentRP;

    protected RPBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);

    }

    public void setMaximumPoints(long maximumRP) {
        this.maximumRP = maximumRP;
        if (getStoredPoints() > getMaximumPoints()) {
            currentRP = getMaximumPoints();
            storedPointsChanged();
        }
    }

    @Override
    @Range(from = 0, to = Long.MAX_VALUE)
    public long getStoredPoints() {
        return this.currentRP;
    }

    @Override
    @Range(from = 1, to = Long.MAX_VALUE)
    public long getMaximumPoints() {
        return this.maximumRP;
    }

    @Range(from = 0, to = Long.MAX_VALUE)
    protected long getRPInsertLimit(){
        return getNeededPoints();
    }

    @Range(from = 0, to = Long.MAX_VALUE)
    protected long getRPExtractLimit(){
        return getStoredPoints();
    }

    protected boolean canAcceptPoints() {
        return true;
    }

    protected boolean canProvidePoints() {
        return true;
    }

    @Override
    public long extractPoints(long toExtract, RPAction action) {
        if (toExtract < 0){
            return insertPoints(-toExtract, action);
        }
        if (canProvidePoints()) {
            return forceExtractPoints(Math.min(getRPExtractLimit(), toExtract), action);
        }
        return 0;
    }

    @Override
    public long insertPoints(long toAccept, RPAction action) {
        if (toAccept < 0){
            return extractPoints(-toAccept, action);
        }
        if (canAcceptPoints()){

        }
        return 0;
    }

    protected long forceExtractPoints(long toExtract, RPAction action) {
        if (toExtract < 0) {
            return forceInsertPoints(-toExtract, action);
        }
        long toRemove = Math.min(getStoredPoints(), toExtract);
        if (action.execute()) {
            currentRP -= toRemove;
            storedPointsChanged();
        }
        return toRemove;
    }

    protected long forceInsertPoints(long toAccept, RPAction action) {
        if (toAccept < 0) {
            return forceExtractPoints(-toAccept, action);
        }
        long toAdd = Math.min(getNeededPoints(), toAccept);
        if (action.execute()) {
            currentRP += toAdd;
            storedPointsChanged();
        }
        return toAdd;
    }

    protected void storedPointsChanged() {
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (getStoredPoints() > getMaximumPoints()){
            currentRP = getMaximumPoints();
        }
        tag.putLong("RP", getStoredPoints());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        long set = tag.getLong("RP");
        if (set > getMaximumPoints()){
            set = getMaximumPoints();
        }
        currentRP = set;
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
}
