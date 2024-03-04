package com.robocraft999.traidingnetwork.blockentity;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.menu.ShopMenu;
import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import com.robocraft999.traidingnetwork.net.IShopNetworkSync;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ShopBlockEntity extends BlockEntity implements IShopNetworkSync, MenuProvider {

    private boolean downwards;
    private EnumSortType sort = EnumSortType.NAME;
    private boolean autoFocus = true;

    public ShopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(TNBlockEntities.SHOP.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        TraidingNetwork.LOGGER.info("Shop+load: " + compound);
        autoFocus = compound.getBoolean("autoFocus");
        setDownwards(compound.getBoolean("dir"));
        setSort(EnumSortType.values()[compound.getInt("sort")]);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("dir", isDownwards());
        compound.putInt("sort", getSort().ordinal());
        compound.putBoolean("autoFocus", autoFocus);
        TraidingNetwork.LOGGER.info("Shop+save: " + compound);
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

    public boolean getAutoFocus() {
        return autoFocus;
    }

    @Override
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("test_name2");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new ShopMenu(playerInventory, i, getLevel(), getBlockPos());
    }
}
