package com.robocraft999.amazingtrading;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = AmazingTrading.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Path CONFIG_DIR;
    static {
        CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(AmazingTrading.NAME));
    }

    public static final ForgeConfigSpec.IntValue SHREDDER_PROCESS_TICKS = BUILDER
            .comment("How many steps the shredder needs to crush one item (steps per tick is based of the rotation speed)")
            .defineInRange("shredderProcessTicks", 200, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue SHREDDER_RPM_TO_SPEED_QUOTIENT = BUILDER
            .comment("Quotient by which the rotation speed is divided to get the steps per tick of processing (higher value increases the time to crush one item)")
            .defineInRange("rpmToSpeedQuotient", 16f, 1f, 1024f);

    public static final ForgeConfigSpec.DoubleValue ITEM_BUY_COST_INCREASE_FACTOR = BUILDER
            .comment("By which factor the cost of buying items in the shop increases from their value when being crushed")
            .defineInRange("itemBuyCostFactor", 2f, 1f, 1000f);

    static final ForgeConfigSpec SPEC = BUILDER.build();
    static final String COMMON_CONFIG_PATH = String.format("%s/%s-common.toml", AmazingTrading.NAME ,AmazingTrading.MODID);

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {

    }
}
