package com.robocraft999.amazingtrading.client.gui.shredderhopper;

import com.robocraft999.amazingtrading.api.capabilities.IResourcePointProvider;
import com.robocraft999.amazingtrading.client.gui.menu.ATInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;

public class ShredderHopperInventory extends ATInventory {

    public ShredderHopperInventory(Player player){
        super(player);
        if (!isServer()) {
            updateClientTargets();
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        super.setStackInSlot(slot, stack);
        if (isServer()) {
            syncChangedSlots(Collections.singletonList(slot), IResourcePointProvider.TargetUpdateType.ALL);
        }
    }
}