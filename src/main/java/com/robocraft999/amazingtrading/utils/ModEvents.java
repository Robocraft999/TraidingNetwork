package com.robocraft999.amazingtrading.utils;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.api.capabilities.IResourceItemProvider;
import com.robocraft999.amazingtrading.api.capabilities.IResourcePointProvider;
import com.robocraft999.amazingtrading.api.capabilities.impl.ResourceItemProviderImpl;
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
