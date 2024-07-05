package com.robocraft999.amazingtrading.net.packets.shredderhopper;

import com.robocraft999.amazingtrading.content.shredderhopper.CreateShredderHopperBlockEntity;
import com.robocraft999.amazingtrading.net.ITNPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SyncOwnerNameHopperPKT implements ITNPacket {
    private String name;
    private BlockPos pos;

    public SyncOwnerNameHopperPKT(String name, BlockPos pos){
        this.name = name;
        this.pos = pos;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player.level().getBlockEntity(pos) instanceof CreateShredderHopperBlockEntity be){
            be.setOwnerName(name);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(name);
        buffer.writeBlockPos(pos);
    }

    public static SyncOwnerNameHopperPKT decode(FriendlyByteBuf buf) {
        return new SyncOwnerNameHopperPKT(buf.readUtf(), buf.readBlockPos());
    }
}
