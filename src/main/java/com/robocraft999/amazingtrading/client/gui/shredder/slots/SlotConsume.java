package com.robocraft999.amazingtrading.client.gui.shredder.slots;

import com.robocraft999.amazingtrading.client.gui.menu.slots.InventoryContainerSlot;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderInventory;
import com.robocraft999.amazingtrading.utils.ResourcePointHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Random;

public class SlotConsume extends InventoryContainerSlot {
    private final ShredderInventory inv;
    private final Random random = new Random();

    public SlotConsume(ShredderInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (inv.isServer() && !stack.isEmpty()) {
            long rpValue = ResourcePointHelper.getRPSellValue(stack);
            if (rpValue == 0) {
                // 50% chance to give 1 RP for items without RP value
                if (random.nextBoolean()) {
                    rpValue = 1;
                }
            }
            inv.addResourcePoints(BigInteger.valueOf(rpValue).multiply(BigInteger.valueOf(stack.getCount())));
            this.setChanged();
        }
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        // Allow items without RP value to be placed in the slot
        return true;
    }
}