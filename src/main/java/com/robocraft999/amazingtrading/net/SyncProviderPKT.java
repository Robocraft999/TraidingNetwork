package com.robocraft999.amazingtrading.net;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderMenu;
import com.robocraft999.amazingtrading.registry.TNCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SyncProviderPKT(CompoundTag nbt) implements ITNPacket {

    @Override
    public void handle(NetworkEvent.Context context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.deserializeNBT(nbt);
                AmazingTrading.LOGGER.debug("** R ** " + cap.getPoints());
                if (player.containerMenu instanceof ShredderMenu container) {
                    container.shredderInventory.updateClientTargets();
                }
            });
        }
        AmazingTrading.LOGGER.debug("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    public static SyncProviderPKT decode(FriendlyByteBuf buffer) {
        return new SyncProviderPKT(buffer.readNbt());
    }
}