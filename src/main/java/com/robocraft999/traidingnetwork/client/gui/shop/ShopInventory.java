package com.robocraft999.traidingnetwork.client.gui.shop;

import com.robocraft999.traidingnetwork.api.capabilities.IResourceItemProvider;
import com.robocraft999.traidingnetwork.client.gui.menu.TNInventory;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.Arrays;
import java.util.List;

public class ShopInventory extends TNInventory {

    public final IResourceItemProvider itemProvider;

    public ShopInventory(Player player){
        super(player, (IItemHandlerModifiable) player.level().getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).orElseThrow(NullPointerException::new).getSlotsHandler());
        this.itemProvider = player.level().getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).orElseThrow(NullPointerException::new);
        if (isServer()){
            itemProvider.sync();
            syncChangedSlots(Arrays.asList(1), IResourceItemProvider.TargetUpdateType.ALL);
        }
    }

    public void syncChangedSlots(List<Integer> slotsChanged, IResourceItemProvider.TargetUpdateType updateTargets) {
        itemProvider.syncSlots((ServerPlayer) player, slotsChanged, updateTargets);
    }
}
