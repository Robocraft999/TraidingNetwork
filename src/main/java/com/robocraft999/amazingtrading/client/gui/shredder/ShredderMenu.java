package com.robocraft999.amazingtrading.client.gui.shredder;

import com.robocraft999.amazingtrading.client.gui.menu.ATContainerMenu;
import com.robocraft999.amazingtrading.registry.ATMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

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
        addPlayerInventory(8, 51);

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
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        Slot currentSlot = tryGetSlot(pIndex);
        Slot inputSlot = this.inputSlots.get(0);

        /*if (currentSlot != null && inputSlot instanceof SlotItemHandler slotItemHandler) {
            ItemStack remaining = ItemHandlerHelper.insertItem(slotItemHandler.getItemHandler(), currentSlot.getItem(), false);
            if (remaining.isEmpty()){
                return ItemStack.EMPTY;
            }
        }*/

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
