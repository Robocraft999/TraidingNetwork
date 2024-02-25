package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.menu.ShopMenu;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SyncItemProviderPKT(CompoundTag nbt) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
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
        TraidingNetwork.LOGGER.debug("** RECEIVED SHOP DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    public static SyncItemProviderPKT decode(FriendlyByteBuf buffer) {
        return new SyncItemProviderPKT(buffer.readNbt());
    }
}
