package com.robocraft999.amazingtrading.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ITNPacket {
    void handle(NetworkEvent.Context context);

    void encode(FriendlyByteBuf buffer);

    static <PACKET extends ITNPacket> void handle(final PACKET message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> message.handle(context));
        context.setPacketHandled(true);
    }
}
