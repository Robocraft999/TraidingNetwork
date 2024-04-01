package com.robocraft999.amazingtrading.net;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.net.SyncResourcePointPKT.ResourcePointPKTInfo;
import com.robocraft999.amazingtrading.net.packets.shop.ShopRequestPKT;
import com.robocraft999.amazingtrading.net.packets.shop.SyncClientSettingsPKT;
import com.robocraft999.amazingtrading.net.packets.shop.SyncSettingsPKT;
import com.robocraft999.amazingtrading.net.packets.shredder.SyncOwnerNamePKT;
import com.robocraft999.amazingtrading.resourcepoints.mapper.RPMappingHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(4);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(TraidingNetwork.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    private static int index;

    public static void register() {
        registerServerToClient(SyncResourcePointPKT.class, SyncResourcePointPKT::decode);
        registerServerToClient(SyncProviderResourcePointPKT.class, SyncProviderResourcePointPKT::decode);
        registerServerToClient(SyncInputsAndLocksPKT.class, SyncInputsAndLocksPKT::decode);
        registerServerToClient(SyncProviderPKT.class, SyncProviderPKT::decode);
        registerServerToClient(SyncSlotsPKT.class, SyncSlotsPKT::decode);
        registerServerToClient(SyncItemProviderPKT.class, SyncItemProviderPKT::decode);
        registerServerToClient(SyncClientSettingsPKT.class, SyncClientSettingsPKT::decode);

        registerServerToClient(SyncOwnerNamePKT.class, SyncOwnerNamePKT::decode);

        registerClientToServer(SyncSettingsPKT.class, SyncSettingsPKT::decode);
        registerClientToServer(ShopRequestPKT.class, ShopRequestPKT::decode);
    }

    private static <MSG extends ITNPacket> void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    private static <MSG extends ITNPacket> void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <MSG extends ITNPacket> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection networkDirection) {
        HANDLER.registerMessage(index++, type, ITNPacket::encode, decoder, ITNPacket::handle, Optional.of(networkDirection));
    }

    private static boolean isLocal(ServerPlayer player) {
        return player.server.isSingleplayerOwner(player.getGameProfile());
    }

    private static void sendFragmentedRpPacket(ServerPlayer player, SyncResourcePointPKT pkt) {
        if (!isLocal(player)) {
            sendTo(pkt, player);
        }
    }

    public static void sendFragmentedRpPacket(ServerPlayer player) {
        sendFragmentedRpPacket(player, new SyncResourcePointPKT(serializeResourcePointsData()));
    }

    public static void sendFragmentedRpPacketToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            SyncResourcePointPKT pkt = new SyncResourcePointPKT(serializeResourcePointsData());
            sendToAll(pkt);
        }
    }

    private static ResourcePointPKTInfo[] serializeResourcePointsData() {
        ResourcePointPKTInfo[] data = RPMappingHandler.createPacketData();
        //Simulate encoding the RP packet to get an accurate size
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        int index = buf.writerIndex();
        new SyncResourcePointPKT(data).encode(buf);
        TraidingNetwork.LOGGER.debug("RP data size: {} bytes", buf.writerIndex() - index);
        buf.release();
        return data;
    }

    /**
     * Send a packet to a specific player.<br> Must be called Server side.
     */
    public static <MSG extends ITNPacket> void sendTo(MSG msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            HANDLER.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }

    public static <MSG extends ITNPacket> void sendToNear(MSG msg, ServerPlayer player) {
        if (player.level() != null){
            HANDLER.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(player.getX(), player.getY(), player.getZ(), 25, player.level().dimension())), msg);
        }
    }

    public static <MSG extends ITNPacket> void sendToNear(MSG msg, BlockPos pos, Level level) {
        if (level != null){
            HANDLER.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 25, level.dimension())), msg);
        }
    }


    public static <MSG extends ITNPacket> void sendToAll(MSG msg) {
        HANDLER.send(PacketDistributor.ALL.noArg(), msg);
    }



    /**
     * Send a packet to the server.
     */
    public static <MSG extends ITNPacket> void sendToServer(MSG msg) {
        HANDLER.sendToServer(msg);
    }
}
