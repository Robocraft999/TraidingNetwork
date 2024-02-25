package com.robocraft999.traidingnetwork.utils;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourceItemProvider;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.api.capabilities.impl.ResourceItemProviderImpl;
import com.robocraft999.traidingnetwork.api.capabilities.impl.ResourcePointProviderImpl;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TraidingNetwork.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event){
        event.register(IResourcePointProvider.class);
        event.register(IResourceItemProvider.class);
    }

    @SubscribeEvent
    public static void attachLevelCaps(AttachCapabilitiesEvent<Level> evt){
        var cap = new ResourceItemProviderImpl.Provider(evt.getObject());
        evt.addCapability(ResourceItemProviderImpl.Provider.NAME, cap);
        evt.addListener(cap::invalidateAll);
    }
}
