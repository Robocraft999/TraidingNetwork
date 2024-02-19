package com.robocraft999.traidingnetwork.utils;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.api.capabilities.impl.ResourcePointProviderImpl;
import com.robocraft999.traidingnetwork.api.capabilities.impl.ShredderOffline;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

@Mod.EventBusSubscriber(modid = TraidingNetwork.MODID)
public class PlayerEvents {
    // On death or return from end, copy the capability data
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        //Revive the player's caps
        original.reviveCaps();
        original.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(old -> {
            CompoundTag knowledge = old.serializeNBT();
            event.getEntity().getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.deserializeNBT(knowledge));
        });
        //Re-invalidate the player's caps now that we copied ours over
        original.invalidateCaps();
    }

    // On death or return from end, sync to the client
    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.sync(player));
        }
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Sync to the client for "normal" interdimensional teleports (nether portal, etc.)
            player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.sync(player));
        }
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof Player player) {
            var cap = new ResourcePointProviderImpl.Provider(player);
            evt.addCapability(ResourcePointProviderImpl.Provider.NAME, cap);
            evt.addListener(cap::invalidateAll);
        }
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event){
        event.register(IResourcePointProvider.class);
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PacketHandler.sendFragmentedEmcPacket(player);

        player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(knowledge -> {
            knowledge.sync(player);
            TraidingNetwork.LOGGER.info("p"+knowledge.getPoints());
            //PlayerHelper.updateScore(player, PlayerHelper.SCOREBOARD_EMC, knowledge.getEmc());
        });

        TraidingNetwork.LOGGER.debug("Sent knowledge and bag data to {}", player.getName());
    }

    @SubscribeEvent
    public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(knowledge -> {
            TraidingNetwork.LOGGER.info("pp"+knowledge.getPoints());
        });
    }

    @SubscribeEvent
    public static void onConstruct(EntityEvent.EntityConstructing evt) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER // No world to check yet
                && evt.getEntity() instanceof Player && !(evt.getEntity() instanceof FakePlayer)) {
            ShredderOffline.clear(evt.getEntity().getUUID());
            TraidingNetwork.LOGGER.debug("Clearing offline data cache in preparation to load online data");
        }
    }
}
