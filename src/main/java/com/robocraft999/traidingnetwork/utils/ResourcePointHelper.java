package com.robocraft999.traidingnetwork.utils;

import com.robocraft999.traidingnetwork.api.ItemInfo;
import com.robocraft999.traidingnetwork.resourcepoints.mapper.RPMappingHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public class ResourcePointHelper {

    public static long getResourcePointValue(ItemStack stack){
        return stack.isEmpty() ? 0 : getEmcValue(ItemInfo.fromStack(stack));
    }

    public static long getEmcValue(@NotNull ItemInfo info) {
        //TODO: Fix this, as it does not catch the edge case that we have an exact match and then there is random added NBT on top of it
        // but that can be thought about more once we have the first pass complete. For example if someone put an enchantment on a potion
        long rpValue = RPMappingHandler.getStoredEmcValue(info);
        if (!info.hasNBT()) {
            HashMap<String, Supplier<String>> v = new HashMap();
            v.values().stream().map(e -> e.get()).toList();
            //If our info has no NBT anyways just return based on the value we got for it
            return rpValue;
        } else if (rpValue == 0) {
            //Try getting a base emc value from the NBT less variant if we don't have one matching our NBT
            rpValue = RPMappingHandler.getStoredEmcValue(ItemInfo.fromItem(info.getItem()));
            if (rpValue == 0) {
                //The base item doesn't have an EMC value either so just exit
                return 0;
            }
        }

        //Note: We continue to use our initial ItemInfo so that we are calculating based on the NBT
        /*for (INBTProcessor processor : processors) {
            if (NBTProcessorConfig.isEnabled(processor)) {
                try {
                    rpValue = processor.recalculateEMC(info, rpValue);
                } catch (ArithmeticException e) {
                    //Return the last successfully calculated EMC value
                    return rpValue;
                }
                if (rpValue <= 0) {
                    //Exit if it gets to zero (also safety check for less than zero in case a mod didn't bother sanctifying their data)
                    return 0;
                }
            }
        }*/
        return rpValue;
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
