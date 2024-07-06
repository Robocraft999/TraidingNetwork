package com.robocraft999.amazingtrading.client.gui.shredderhopper;

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

public class ShredderHopperMenu extends ATContainerMenu {
    public final ShredderHopperInventory shredderHopperInventory;
    protected final List<Slot> inputSlotsHopper = new ArrayList<>();
    public final BlockPos blockPos;

    public ShredderHopperMenu(Inventory playerInv, int i, BlockPos blockPos) {
        super(ATMenuTypes.SHREDDER_MENU.get(), playerInv, i);
        this.shredderHopperInventory = new ShredderHopperInventory(playerInv.player);
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
                    this.inputSlotsHopper.add(inputSlot);
                });
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        Slot currentSlot = tryGetSlot(pIndex);
        Slot inputSlot = this.inputSlotsHopper.get(0);

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = tryGetSlot(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.inputSlotsHopper.size()) {
                if (!this.moveItemStackTo(itemstack1, this.inputSlotsHopper.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.inputSlotsHopper.size(), false)) {
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

    // Add this getter method
    public List<Slot> getInputSlotsHopper() {
        return inputSlotsHopper;
    }
}
