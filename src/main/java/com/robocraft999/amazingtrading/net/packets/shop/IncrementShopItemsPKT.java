package com.robocraft999.amazingtrading.net.packets.shop;

import com.robocraft999.amazingtrading.net.ITNPacket;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class IncrementShopItemsPKT implements ITNPacket {
    private final int slot;
    private final int count;

    public IncrementShopItemsPKT(int slot, int count) {
        this.slot = slot;
        this.count = count;
    }

    public static void encode(IncrementShopItemsPKT msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeInt(msg.count);
    }

    public static IncrementShopItemsPKT decode(FriendlyByteBuf buf) {
        return new IncrementShopItemsPKT(buf.readInt(), buf.readInt());
    }

    public static void handle(IncrementShopItemsPKT msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(ATCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(cap -> {
                    IItemHandler itemHandler = cap.getSlotsHandler();
                    ItemStack itemStack = itemHandler.getStackInSlot(msg.slot);
                    if (!itemStack.isEmpty()) {
                        itemStack.grow(msg.count);
                        itemHandler.insertItem(msg.slot, itemStack, false);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeInt(count);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        handle(this, () -> context);
    }
}
