package com.robocraft999.traidingnetwork.utils;

import net.minecraft.world.item.ItemStack;

public class ResourcePointHelper {

    public static long getResourcePointValue(ItemStack stack){
        return 0;
    }

    public static long getRPSellValue(ItemStack stack){
        return 10;
    }

    public static boolean doesItemHaveRP(ItemStack stack){
        return getRPSellValue(stack) > 0;
    }
}
