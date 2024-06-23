package com.robocraft999.amazingtrading;

import com.mojang.logging.LogUtils;
import com.robocraft999.amazingtrading.commands.IncrementShopItemsCommand;
import com.robocraft999.amazingtrading.kubejs.ATKubeJSPlugin;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.registry.*;
import com.robocraft999.amazingtrading.resourcepoints.mapper.CustomRPParser;
import com.robocraft999.amazingtrading.resourcepoints.mapper.RPMappingHandler;
import com.robocraft999.amazingtrading.resourcepoints.mapper.recipe.CraftingMapper;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSItem;
import com.robocraft999.amazingtrading.resourcepoints.nss.NSSSerializer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AmazingTrading.MODID)
public class AmazingTrading {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "amazingtrading";

    //public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MODID));
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public static final String NAME = "AmazingTrading";

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public static final RegistryEntry<CreativeModeTab> testcreativetab = REGISTRATE.object(ATLang.NAME_CREATIVE_TAB)
            .defaultCreativeTab(tab -> tab.withLabelColor(0xFF00AA00))
            .register();


    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AmazingTrading() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::imcQueue);
        modEventBus.addListener(this::clientSetup);
        REGISTRATE.registerEventListeners(modEventBus);

        ATBlocks.register();
        ATBlockEntities.register();
        ATMenuTypes.register();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ATPartials::init);

        PacketHandler.register();
        ATLang.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::tagsUpdated);
        if (ModList.get().isLoaded("kubejs")){
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, (TagsUpdatedEvent event) -> ATKubeJSPlugin.onServerReload(event));
        }

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        RPMappingHandler.loadMappers();
        CraftingMapper.loadMappers();

        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void clientSetup(final FMLClientSetupEvent event) {}

    public static ResourceLocation rl(String name){
        return new ResourceLocation(MODID, name);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");

        // Register commands
        IncrementShopItemsCommand.register(event.getServer().getCommands().getDispatcher());
    }

    private void imcQueue(InterModEnqueueEvent event){
        NSSSerializer.init();
    }

    private record RPUpdateData(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
    }
    @Nullable
    private RPUpdateData rpUpdateResourceManager;

    private void tagsUpdated(TagsUpdatedEvent event) {
        if (rpUpdateResourceManager != null) {
            long start = System.currentTimeMillis();
            //Clear the cached created tags
            NSSItem.clearCreatedTags();
            CustomRPParser.init();
            try {
                RPMappingHandler.map(rpUpdateResourceManager.serverResources(), rpUpdateResourceManager.registryAccess(), rpUpdateResourceManager.resourceManager());
                AmazingTrading.LOGGER.info("Registered {} RP values. (took {} ms)", RPMappingHandler.getRpMapSize(), System.currentTimeMillis() - start);
                PacketHandler.sendFragmentedRpPacketToAll();
            } catch (Throwable t) {
                AmazingTrading.LOGGER.error("Error calculating RP values", t);
            }
            rpUpdateResourceManager = null;
        }
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) manager -> rpUpdateResourceManager = new RPUpdateData(event.getServerResources(), event.getRegistryAccess(), manager));
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
        }
    }
}