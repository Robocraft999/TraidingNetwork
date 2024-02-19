package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public class ShopMenu extends TNContainerMenu {
    public ShopMenu(Inventory playerInv, int i) {
        super(TNMenuTypes.SHOP_MENU.get(), playerInv, i);
        initSlots();
    }

    protected void initSlots() {
        addPlayerInventory(8, 84);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
}
