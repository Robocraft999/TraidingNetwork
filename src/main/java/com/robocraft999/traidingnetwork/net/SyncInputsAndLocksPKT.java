package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider.TargetUpdateType;
import com.robocraft999.traidingnetwork.gui.menu.ShredderInventory;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;

public record SyncInputsAndLocksPKT(Map<Integer, ItemStack> stacksToSync, TargetUpdateType updateTargets) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.receiveInputsAndLocks(stacksToSync);
                if (updateTargets != TargetUpdateType.NONE && player.containerMenu instanceof ShredderMenu container) {
                    //Update targets in case total available EMC is now different
                    ShredderInventory transmutationInventory = container.shredderInventory;
                    if (updateTargets == TargetUpdateType.ALL) {
                        transmutationInventory.updateClientTargets();
                    } else {//If needed
                        transmutationInventory.checkForUpdates();
                    }
                }
            });
        }
        //PECore.debugLog("** RECEIVED TRANSMUTATION INPUT AND LOCK DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(stacksToSync.size());
        for (Map.Entry<Integer, ItemStack> entry : stacksToSync.entrySet()) {
            buffer.writeVarInt(entry.getKey());
            buffer.writeItem(entry.getValue());
        }
        buffer.writeEnum(updateTargets);
    }

    public static SyncInputsAndLocksPKT decode(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        Map<Integer, ItemStack> syncedStacks = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            syncedStacks.put(buffer.readVarInt(), buffer.readItem());
        }
        return new SyncInputsAndLocksPKT(syncedStacks, buffer.readEnum(TargetUpdateType.class));
    }
}