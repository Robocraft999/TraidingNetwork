package com.robocraft999.traidingnetwork.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper {
    public static IItemHandlerModifiable immutableCopy(IItemHandler toCopy) {
        final List<ItemStack> list = new ArrayList<>(toCopy.getSlots());
        for (int i = 0; i < toCopy.getSlots(); i++) {
            list.add(toCopy.getStackInSlot(i));
        }
        return new IItemHandlerModifiable() {
            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            }

            @Override
            public int getSlots() {
                return list.size();
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return list.get(slot);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack;
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return getStackInSlot(slot).getMaxStackSize();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return true;
            }
        };
    }
}
