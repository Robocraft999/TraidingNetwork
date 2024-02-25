package com.robocraft999.traidingnetwork.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ResourcePointHelper {

    public static long getResourcePointValue(ItemStack stack){
        return 20;
    }

    public static long getRPSellValue(ItemStack stack){
        if (stack.isEmpty())
            return 0;
        return 10;
    }

    public static boolean doesItemHaveRP(ItemStack stack){
        return getRPSellValue(stack) > 0;
    }
}
