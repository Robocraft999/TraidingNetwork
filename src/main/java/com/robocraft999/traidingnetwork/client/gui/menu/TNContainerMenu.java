package com.robocraft999.traidingnetwork.client.gui.menu;

import com.robocraft999.traidingnetwork.client.gui.menu.slots.HotBarSlot;
import com.robocraft999.traidingnetwork.client.gui.menu.slots.InventoryContainerSlot;
import com.robocraft999.traidingnetwork.client.gui.menu.slots.MainInventorySlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TNContainerMenu extends AbstractContainerMenu {

    protected final Inventory playerInv;
    protected final List<Slot> inventoryContainerSlots = new ArrayList<>();
    protected final List<Slot> mainInventorySlots = new ArrayList<>();
    protected final List<Slot> hotBarSlots = new ArrayList<>();

    protected TNContainerMenu(@Nullable MenuType<?> type, Inventory playerInv, int i) {
        super(type, i);
        this.playerInv = playerInv;
    }

    protected void addPlayerInventory(int xStart, int yStart) {
        int slotSize = 18;
        int rows = 3;
        //Main Inventory
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(createMainInventorySlot(playerInv, j + i * 9 + 9, xStart + j * slotSize, yStart + i * slotSize));
            }
        }
        yStart = yStart + slotSize * rows + 4;
        //Hot Bar
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            addSlot(createHotBarSlot(playerInv, i, xStart + i * slotSize, yStart));
        }
    }

    protected MainInventorySlot createMainInventorySlot(@NotNull Inventory inv, int index, int x, int y) {
        return new MainInventorySlot(inv, index, x, y);
    }

    protected HotBarSlot createHotBarSlot(@NotNull Inventory inv, int index, int x, int y) {
        return new HotBarSlot(inv, index, x, y);
    }

    @NotNull
    @Override
    protected Slot addSlot(@NotNull Slot slot) {
        super.addSlot(slot);
        if (slot instanceof InventoryContainerSlot containerSlot) {
            inventoryContainerSlots.add(containerSlot);
        } else if (slot instanceof MainInventorySlot inventorySlot) {
            mainInventorySlots.add(inventorySlot);
        } else if (slot instanceof HotBarSlot hotBarSlot) {
            hotBarSlots.add(hotBarSlot);
        }
        return slot;
    }

    @Nullable
    public Slot tryGetSlot(int slotId) {
        if (slotId >= 0 && slotId < slots.size()) {
            return getSlot(slotId);
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
