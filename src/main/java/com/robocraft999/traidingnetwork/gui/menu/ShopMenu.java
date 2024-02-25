package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.slots.ShopSlot;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu extends TNContainerMenu {
    //CombinedInvWrapper wrapper;
    public final ShopInventory shopInventory;

    protected final List<Slot> itemSlots = new ArrayList<>();

    public ShopMenu(Inventory playerInv, int i) {
        super(TNMenuTypes.SHOP_MENU.get(), playerInv, i);
        shopInventory = new ShopInventory(playerInv.player);
        initSlots(playerInv.player);
    }

    protected void initSlots(Player player) {
        addPlayerInventory(8, 84);
        addShopSlots();
    }

    private void addShopSlots(){
        //NonNullList<ItemStack> stacks = handler.getStacks();
        int amount = shopInventory.getSlots();
        int i = 0;
        int xStart = 8;
        int yStart = 18;
        for (int y = 0; y < amount/9 + 1; y++){
            for (int x = 0; x < Math.min(amount - 9 * y, 9); x++){
                addSlot(new ShopSlot(shopInventory, i, xStart + x*18, yStart + y*18));
                i++;
            }
        }
    }

    @Override
    protected @NotNull Slot addSlot(@NotNull Slot slot) {
        if (slot instanceof ShopSlot input) {
            itemSlots.add(input);
        }
        return super.addSlot(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
}
