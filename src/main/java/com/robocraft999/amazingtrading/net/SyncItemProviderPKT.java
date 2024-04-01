package com.robocraft999.amazingtrading.net;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.client.gui.shop.ShopMenu;
import com.robocraft999.amazingtrading.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SyncItemProviderPKT(CompoundTag nbt) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            ClientLevel level = player.clientLevel;
            if (player != null && level != null) {
                level.getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(cap -> {
                    cap.deserializeNBT(nbt);
                    if (player.containerMenu instanceof ShopMenu container) {
                        container.shopInventory.updateClientTargets();
                    }
                });
            }
        });
        TraidingNetwork.LOGGER.debug("** RECEIVED SHOP DATA CLIENTSIDE **");
        context.setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    public static SyncItemProviderPKT decode(FriendlyByteBuf buffer) {
        return new SyncItemProviderPKT(buffer.readNbt());
    }
}
