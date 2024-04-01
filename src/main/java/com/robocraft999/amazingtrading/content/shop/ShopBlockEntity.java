package com.robocraft999.amazingtrading.content.shop;

import com.robocraft999.amazingtrading.client.gui.shop.ShopMenu;
import com.robocraft999.amazingtrading.registry.ATBlockEntities;
import com.robocraft999.amazingtrading.registry.ATLang;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ShopBlockEntity extends KineticBlockEntity implements MenuProvider {

    public ShopBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(ATBlockEntities.SHOP.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ATLang.KEY_SHOP_GUI_NAME);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new ShopMenu(playerInventory, i, getLevel(), getBlockPos());
    }
}
