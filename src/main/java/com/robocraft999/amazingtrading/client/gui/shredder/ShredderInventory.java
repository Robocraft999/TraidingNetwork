package com.robocraft999.amazingtrading.client.gui.shredder;

import com.robocraft999.amazingtrading.client.gui.menu.ATInventory;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
inspired by https://github.com/sinkillerj/ProjectE/blob/mc1.20.x/src/main/java/moze_intel/projecte/gameObjs/container/inventory/TransmutationInventory.java
 */
public class ShredderInventory extends ATInventory {

    private final IItemHandlerModifiable inputHandler;

    public ShredderInventory(Player player){
        super(player, (IItemHandlerModifiable) player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).orElseThrow(NullPointerException::new).getInputAndLocks());
        this.inputHandler = itemHandler[0];
        if (!isServer()) {
            updateClientTargets();
        }
    }
}
