package com.robocraft999.traidingnetwork.gui.slots;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.menu.ShredderInventory;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class SlotConsume extends InventoryContainerSlot{
    ShredderInventory inv;

    public SlotConsume(ShredderInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (inv.isServer() && !stack.isEmpty()) {
            //inv.handleKnowledge(stack);
            inv.addResourcePoints(BigInteger.valueOf(ResourcePointHelper.getRPSellValue(stack)).multiply(BigInteger.valueOf(stack.getCount())));
            this.setChanged();
        }
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return ResourcePointHelper.doesItemHaveRP(stack);
    }
}
