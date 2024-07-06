package com.robocraft999.amazingtrading.client.gui.shredder;

import com.robocraft999.amazingtrading.client.gui.menu.ATContainerMenu;
import com.robocraft999.amazingtrading.registry.ATMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ShredderMenu extends ATContainerMenu {
    public final ShredderInventory shredderInventory;
    protected final List<Slot> inputSlots = new ArrayList<>();
    public final BlockPos blockPos;

    public ShredderMenu(Inventory playerInv, int i, BlockPos blockPos) {
        super(ATMenuTypes.SHREDDER_MENU.get(), playerInv, i);
        this.shredderInventory = new ShredderInventory(playerInv.player);
        this.blockPos = blockPos;
        initSlots();
    }

    protected void initSlots(){
        if (getLevel() != null){
            var blockEntity = getLevel().getBlockEntity(blockPos);
            if (blockEntity != null) {
                blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    Slot inputSlot = new SlotItemHandler(handler, 0, 152, 32);
                    this.addSlot(inputSlot);
                    this.inputSlots.add(inputSlot);
                });
            }
        }

        addPlayerInventory(8, 51);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = tryGetSlot(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.inputSlots.size()) {
                if (!this.moveItemStackTo(itemstack1, this.inputSlots.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

            } else if (!this.moveItemStackTo(itemstack1, 0, this.inputSlots.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
