package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.blockentity.ShopBlockEntity;
import com.robocraft999.traidingnetwork.gui.slots.shop.ShopSlot;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu extends TNContainerMenu{
    public final ShopInventory shopInventory;
    public final ShopBlockEntity blockEntity;

    protected final List<Slot> itemSlots = new ArrayList<>();

    public ShopMenu(Inventory playerInv, int i, Level level, BlockPos blockPos) {
        super(TNMenuTypes.SHOP_MENU.get(), playerInv, i);
        shopInventory = new ShopInventory(playerInv.player);
        TraidingNetwork.LOGGER.info("pos: " + blockPos + " level: " + level);
        this.blockEntity = (ShopBlockEntity) level.getBlockEntity(blockPos);
        initSlots();
    }

    protected void initSlots() {
        //addPlayerInventory(8, 84);
        addPlayerInventory(8, 174);
        //addShopSlots();
    }

    private void addShopSlots(){
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
