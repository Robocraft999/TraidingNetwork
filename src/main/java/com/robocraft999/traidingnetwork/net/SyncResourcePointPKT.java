package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class SyncResourcePointPKT implements ITNPacket{

    private final ResourcePointPKTInfo[] data;

    public SyncResourcePointPKT(ResourcePointPKTInfo[] data) {
        this.data = data;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        TraidingNetwork.LOGGER.info("Receiving EMC data from server.");
        //EMCMappingHandler.fromPacket(data);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(data.length);
        for (ResourcePointPKTInfo info : data) {
            buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, info.item);
            buffer.writeNbt(info.nbt());
            buffer.writeVarLong(info.points());
        }
    }

    public static SyncResourcePointPKT decode(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        ResourcePointPKTInfo[] data = new ResourcePointPKTInfo[size];
        for (int i = 0; i < size; i++) {
            data[i] = new ResourcePointPKTInfo(buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS), buffer.readNbt(), buffer.readVarLong());
        }
        return new SyncResourcePointPKT(data);
    }

    public record ResourcePointPKTInfo(Item item, @Nullable CompoundTag nbt, long points) {}
}
