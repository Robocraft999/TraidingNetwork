package com.robocraft999.amazingtrading.client.gui.shredder;

import com.robocraft999.amazingtrading.client.gui.menu.ATInventory;
import net.minecraft.world.entity.player.Player;

/**
inspired by https://github.com/sinkillerj/ProjectE/blob/mc1.20.x/src/main/java/moze_intel/projecte/gameObjs/container/inventory/TransmutationInventory.java
 */
public class ShredderInventory extends ATInventory {

    public ShredderInventory(Player player){
        super(player);
        if (!isServer()) {
            updateClientTargets();
        }
    }
}
