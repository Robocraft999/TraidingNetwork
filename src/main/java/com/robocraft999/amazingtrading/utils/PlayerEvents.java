package com.robocraft999.amazingtrading.utils;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.capabilities.impl.ResourcePointProviderImpl;
import com.robocraft999.amazingtrading.api.capabilities.impl.ShopSettingsProviderImpl;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

@Mod.EventBusSubscriber(modid = AmazingTrading.MODID)
public class PlayerEvents {
    // On death or return from end, copy the capability data
    @SubscribeEvent
    public static void cloneEvent(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        //Revive the player's caps
        original.reviveCaps();
        original.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(old -> {
            CompoundTag tag = old.serializeNBT();
            event.getEntity().getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.deserializeNBT(tag));
        });
        original.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(old -> {
            CompoundTag tag = old.serializeNBT();
            event.getEntity().getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(c -> c.deserializeNBT(tag));
        });
        //Re-invalidate the player's caps now that we copied ours over
        original.invalidateCaps();
    }

    // On death or return from end, sync to the client
    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.sync(player));
            player.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(c -> c.sync(player));
        }
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Sync to the client for "normal" interdimensional teleports (nether portal, etc.)
            player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(c -> c.sync(player));
            player.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(c -> c.sync(player));
        }
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof Player player) {
            var cap1 = new ResourcePointProviderImpl.Provider(player);
            evt.addCapability(ResourcePointProviderImpl.Provider.NAME, cap1);
            evt.addListener(cap1::invalidateAll);

            var cap2 = new ShopSettingsProviderImpl.Provider(player);
            evt.addCapability(ShopSettingsProviderImpl.Provider.NAME, cap2);
            evt.addListener(cap2::invalidateAll);
        }
    }

    @SubscribeEvent
    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PacketHandler.sendFragmentedRpPacket(player);

        player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(pointProvider -> {
            pointProvider.sync(player);
            AmazingTrading.LOGGER.debug("p"+pointProvider.getPoints());
            PlayerHelper.updateScore(player, PlayerHelper.SCOREBOARD_RP, pointProvider.getPoints());
        });

        player.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(settings -> {
            settings.sync(player);
            AmazingTrading.LOGGER.debug("autofocus: "+settings.getAutoFocus() + " sort: " + settings.getSort() + " downwards: " + settings.isDownwards());
        });

        AmazingTrading.LOGGER.debug("Sent point provider and shop settings to {}", player.getName());
    }

    @SubscribeEvent
    public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(pointProvider -> {
            AmazingTrading.LOGGER.debug("pp"+pointProvider.getPoints());
        });
        player.getCapability(ATCapabilities.SHOP_SETTINGS_CAPABILITY).ifPresent(shop -> {
            AmazingTrading.LOGGER.debug("autofocus: "+shop.getAutoFocus() + " sort: " + shop.getSort() + " downwards: " + shop.isDownwards());
        });
    }

    @SubscribeEvent
    public static void onConstruct(EntityEvent.EntityConstructing evt) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER // No world to check yet
                && evt.getEntity() instanceof Player && !(evt.getEntity() instanceof FakePlayer)) {
            AmazingTrading.LOGGER.debug("Clearing offline data cache in preparation to load online data");
        }
    }
}
