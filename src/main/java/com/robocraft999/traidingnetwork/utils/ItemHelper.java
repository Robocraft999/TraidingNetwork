package com.robocraft999.traidingnetwork.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemHelper {

    private static final Map<Item, String> modNamesForIds = new HashMap<>();


    /**
     * Get mod id for item, but use cache to save time just in case it helps
     *
     * @param theitem
     * @return
     */
    public static String getModNameForItem(Item theitem) {
        if (modNamesForIds.containsKey(theitem)) {
            return modNamesForIds.get(theitem);
        }
        String modId = ForgeRegistries.ITEMS.getKey(theitem).getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        modNamesForIds.put(theitem, lowercaseModId);
        return lowercaseModId;
    }

    public static String formatLargeNumber(int size) {
        if (size < Math.pow(10, 3)) {
            return size + "";
        }
        else if (size < Math.pow(10, 6)) {
            //      float r = (size) / 1000.0F;
            float rounded = Math.round(size / 100.0F) / 10F; //so 1600 => 1.6 and then rounded to become 2.
            return rounded + "K";
        }
        else if (size < Math.pow(10, 9)) {
            int rounded = Math.round(size / (float) Math.pow(10, 6));
            return rounded + "M";
        }
        else if (size < Math.pow(10, 12)) {
            int rounded = Math.round(size / (float) Math.pow(10, 9));
            return rounded + "B";
        }
        return size + "";
    }

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
