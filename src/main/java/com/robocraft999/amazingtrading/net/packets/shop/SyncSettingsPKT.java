package com.robocraft999.amazingtrading.net.packets.shop;

import com.robocraft999.amazingtrading.client.gui.shop.slots.EnumSortType;
import com.robocraft999.amazingtrading.net.ITNPacket;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SyncSettingsPKT implements ITNPacket {

    private boolean direction;
    private EnumSortType sort;
    private boolean autoFocus;

    public SyncSettingsPKT(boolean direction, EnumSortType sort, boolean autoFocus) {
        this.direction = direction;
        this.sort = sort;
        this.autoFocus = autoFocus;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork( () -> {
            ServerPlayer player = context.getSender();
            player.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(cap -> {
                cap.setAutoFocus(autoFocus);
                cap.setSort(sort);
                cap.setDownwards(direction);
            });
        });

    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(direction);
        buffer.writeInt(sort.ordinal());
        buffer.writeBoolean(autoFocus);
    }

    public static SyncSettingsPKT decode(FriendlyByteBuf buf) {
        boolean direction = buf.readBoolean();
        EnumSortType sort = EnumSortType.values()[buf.readInt()];
        boolean autoFocus = buf.readBoolean();
        return new SyncSettingsPKT(direction, sort, autoFocus);
    }
}
