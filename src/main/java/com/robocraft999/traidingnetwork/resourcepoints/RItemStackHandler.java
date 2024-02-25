package com.robocraft999.traidingnetwork.resourcepoints;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class RItemStackHandler extends ItemStackHandler {

    public RItemStackHandler() {
        super(0);
    }

    public void changeSize(int size) {
        NonNullList<ItemStack> newStacks = NonNullList.withSize(((size / 9) + 1) * 9, ItemStack.EMPTY);
        for (int i = 0; i < getSlots(); i++) {
            newStacks.set(i, getStackInSlot(i));
        }
        stacks = newStacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return getSlotLimit(slot);
    }

    public NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == getSlots()) {
            changeSize(slot);
        } else if (slot > getSlots()) {
            throw new UnsupportedOperationException("Slot index out of bounds");
        }
        return super.insertItem(slot, stack, simulate);
    }

    public boolean put(ItemStack stack){
        if (stack == ItemStack.EMPTY)return false;
        for (int i = 0; i < getSlots(); i++){
            if (getStackInSlot(i).getItem() == stack.getItem() || getStackInSlot(i) == ItemStack.EMPTY){
                ItemStack remainder = insertItem(i, stack, false);
                return remainder == ItemStack.EMPTY;
            }
        }
        ItemStack remainder = insertItem(getSlots(), stack, false);
        return remainder == ItemStack.EMPTY;
    }

    public boolean hasFreeSlot(ItemStack stackInSlot) {
        return stacks.stream().anyMatch((stack) -> stack.isEmpty() || stack.is(stackInSlot.getItem()));
    }

    public void enlarge(){
        changeSize(getSlots() + 1);
    }
}
