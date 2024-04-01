package com.robocraft999.amazingtrading.net;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderMenu;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
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
            player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.setPoints(points);
                if (player.containerMenu instanceof ShredderMenu container) {
                    container.shredderInventory.updateClientTargets();
                }
            });
        }
        AmazingTrading.LOGGER.debug("** RECEIVED RP PROVIDER DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(points.toString());
    }

    public static SyncProviderResourcePointPKT decode(FriendlyByteBuf buffer) {
        String rp = buffer.readUtf();
        return new SyncProviderResourcePointPKT(rp.isEmpty() ? BigInteger.ZERO : new BigInteger(rp));
    }
}
