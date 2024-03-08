package com.robocraft999.traidingnetwork.net.packets.shop;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import com.robocraft999.traidingnetwork.net.IShopNetworkSync;
import com.robocraft999.traidingnetwork.net.ITNPacket;
import com.robocraft999.traidingnetwork.net.SyncInputsAndLocksPKT;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;

public class SyncSettingsPKT implements ITNPacket {

    private BlockPos pos;
    private boolean direction;
    private EnumSortType sort;
    private boolean targetTileEntity;
    private boolean autoFocus;

    public SyncSettingsPKT(BlockPos pos, boolean direction, EnumSortType sort, boolean autoFocus) {
        this.pos = pos;
        this.direction = direction;
        this.sort = sort;
        this.autoFocus = autoFocus;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork( () -> {
            ServerPlayer player = context.getSender();
            if (targetTileEntity){
                BlockEntity blockEntity = player.level().getBlockEntity(pos);
                if (blockEntity instanceof IShopNetworkSync sync){
                    TraidingNetwork.LOGGER.info("handle sync settings");
                    sync.setSort(sort);
                    sync.setDownwards(direction);
                    sync.setAutoFocus(autoFocus);
                    blockEntity.setChanged();
                }
            }
        });

    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(direction);
        buffer.writeInt(sort.ordinal());
        if (pos != null) {
            buffer.writeBoolean(true);
            buffer.writeBlockPos(pos);
        }
        else { // to avoid null values // inconsistent buffer size
            buffer.writeBoolean(false);
            buffer.writeBlockPos(BlockPos.ZERO);
        }
        buffer.writeBoolean(autoFocus);
    }

    public static SyncSettingsPKT decode(FriendlyByteBuf buf) {
        boolean direction = buf.readBoolean();
        EnumSortType sort = EnumSortType.values()[buf.readInt()];
        boolean targetTileEntity = buf.readBoolean();
        BlockPos pos = buf.readBlockPos();
        boolean autoFocus = buf.readBoolean();
        SyncSettingsPKT pkt = new SyncSettingsPKT(pos, direction, sort, autoFocus);
        pkt.targetTileEntity = targetTileEntity;
        return pkt;
    }
}
