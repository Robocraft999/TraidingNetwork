package com.robocraft999.amazingtrading.utils;

import com.robocraft999.amazingtrading.Config;
import com.robocraft999.amazingtrading.api.ItemInfo;
import com.robocraft999.amazingtrading.resourcepoints.mapper.RPMappingHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public class ResourcePointHelper {

    public static long getRPBuyCost(ItemStack stack){
        if (stack.isEmpty()) return 0;
        long value = getRPValue(ItemInfo.fromStack(stack));
        return value > 0 ? (long) (value * Config.ITEM_BUY_COST_INCREASE_FACTOR.get()) : 1;
    }

    public static long getRPValue(@NotNull ItemInfo info) {
        //TODO: Fix this, as it does not catch the edge case that we have an exact match and then there is random added NBT on top of it
        // but that can be thought about more once we have the first pass complete. For example if someone put an enchantment on a potion
        long rpValue = RPMappingHandler.getStoredRpValue(info);
        if (!info.hasNBT()) {
            HashMap<String, Supplier<String>> v = new HashMap<>();
            v.values().stream().map(e -> e.get()).toList();
            //If our info has no NBT anyways just return based on the value we got for it
            return rpValue;
        } else if (rpValue == 0) {
            //Try getting a base rp value from the NBT less variant if we don't have one matching our NBT
            rpValue = RPMappingHandler.getStoredRpValue(ItemInfo.fromItem(info.getItem()));
            if (rpValue == 0) {
                //The base item doesn't have an RP value either so just exit
                return 0;
            }
        }
        return rpValue;
    }

    public static long getRPSellValue(ItemStack stack){
        if (stack.isEmpty())
            return 0;
        return getRPValue(ItemInfo.fromStack(stack));
    }

    public static boolean doesItemHaveRP(ItemStack stack){
        return !stack.isEmpty() && getRPValue(ItemInfo.fromStack(stack)) > 0;
    }
}
