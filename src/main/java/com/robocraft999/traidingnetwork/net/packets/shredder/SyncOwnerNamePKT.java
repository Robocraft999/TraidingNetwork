package com.robocraft999.traidingnetwork.net.packets.shredder;

import com.robocraft999.traidingnetwork.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.traidingnetwork.net.ITNPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class SyncOwnerNamePKT implements ITNPacket {
    private String name;
    private BlockPos pos;

    public SyncOwnerNamePKT(String name, BlockPos pos){
        this.name = name;
        this.pos = pos;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player.level().getBlockEntity(pos) instanceof CreateShredderBlockEntity be){
            be.setOwnerName(name);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(name);
        buffer.writeBlockPos(pos);
    }

    public static SyncOwnerNamePKT decode(FriendlyByteBuf buf) {
        return new SyncOwnerNamePKT(buf.readUtf(), buf.readBlockPos());
    }
}
