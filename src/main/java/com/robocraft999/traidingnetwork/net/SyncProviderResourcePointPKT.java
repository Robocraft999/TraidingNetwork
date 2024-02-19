package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.math.BigInteger;

public record SyncProviderResourcePointPKT(BigInteger points) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.setPoints(points);
                if (player.containerMenu instanceof ShredderMenu container) {
                    container.shredderInventory.updateClientTargets();
                }
            });
        }
        TraidingNetwork.LOGGER.debug("** RECEIVED TRANSMUTATION EMC DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(points.toString());
    }

    public static SyncProviderResourcePointPKT decode(FriendlyByteBuf buffer) {
        String emc = buffer.readUtf();
        return new SyncProviderResourcePointPKT(emc.isEmpty() ? BigInteger.ZERO : new BigInteger(emc));
    }
}
