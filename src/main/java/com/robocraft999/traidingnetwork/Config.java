package com.robocraft999.traidingnetwork;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = TraidingNetwork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Path CONFIG_DIR;
    static {
        CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(TraidingNetwork.NAME));
    }

    public static final ForgeConfigSpec.IntValue SHREDDER_PROCESS_TICKS = BUILDER
            .comment("How many ticks the shredder needs at default speed to crush one item")
            .defineInRange("shredderProcessTicks", 200, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue ITEM_BUY_COST_INCREASE_FACTOR = BUILDER
            .comment("By which factor the cost of buying items in the shop increases from their value when being crushed")
            .defineInRange("itemBuyCostFactor", 2f, 1f, 1000f);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {

    }
}
