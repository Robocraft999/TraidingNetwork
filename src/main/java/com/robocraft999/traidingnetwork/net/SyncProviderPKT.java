package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.network.FriendlyByteBuf;

public record SyncProviderPKT(CompoundTag nbt) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.deserializeNBT(nbt);
                TraidingNetwork.LOGGER.debug("** R ** " + cap.getPoints());
                if (player.containerMenu instanceof ShredderMenu container) {
                    container.shredderInventory.updateClientTargets();
                }
            });
        }
        TraidingNetwork.LOGGER.debug("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    public static SyncProviderPKT decode(FriendlyByteBuf buffer) {
        return new SyncProviderPKT(buffer.readNbt());
    }
}