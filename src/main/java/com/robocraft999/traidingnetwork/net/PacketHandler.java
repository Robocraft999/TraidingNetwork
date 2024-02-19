package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.resourcepoints.RPMappingHandler;
import com.robocraft999.traidingnetwork.net.SyncResourcePointPKT.ResourcePointPKTInfo;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

    private static void sendFragmentedEmcPacket(ServerPlayer player, SyncResourcePointPKT pkt/*, SyncFuelMapperPKT fuelPkt*/) {
        if (!isLocal(player)) {
            sendTo(pkt, player);
            //sendTo(fuelPkt, player);
        }
    }

    public static void sendFragmentedEmcPacket(ServerPlayer player) {
        sendFragmentedEmcPacket(player, new SyncResourcePointPKT(serializeResourcePointsData())/*, FuelMapper.getSyncPacket()*/);
    }

    public static void sendFragmentedEmcPacketToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            SyncResourcePointPKT pkt = new SyncResourcePointPKT(serializeResourcePointsData());
            //SyncFuelMapperPKT fuelPkt = FuelMapper.getSyncPacket();
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                sendFragmentedEmcPacket(player, pkt/*, fuelPkt*/);
            }
        }
    }

    private static ResourcePointPKTInfo[] serializeResourcePointsData() {
        ResourcePointPKTInfo[] data = RPMappingHandler.createPacketData();
        //Simulate encoding the EMC packet to get an accurate size
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        int index = buf.writerIndex();
        new SyncResourcePointPKT(data).encode(buf);
        TraidingNetwork.LOGGER.debug("EMC data size: {} bytes", buf.writerIndex() - index);
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
}
