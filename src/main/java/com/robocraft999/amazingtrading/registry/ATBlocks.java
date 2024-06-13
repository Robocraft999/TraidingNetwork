package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.content.shop.ShopBlock;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.material.MapColor;

import static com.robocraft999.amazingtrading.AmazingTrading.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOnly;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ATBlocks {
    public static final BlockEntry<? extends CreateShredderBlock> CREATE_SHREDDER = REGISTRATE
            .block("create_shredder", CreateShredderBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 90))
            .transform(BlockStressDefaults.setImpact(16))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<? extends ShopBlock> SHOP = REGISTRATE
            .block("shop", ShopBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.METAL))
            .transform(axeOnly())
            .blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p), 90))
            .transform(BlockStressDefaults.setImpact(16))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
        // Set the render layer for the SHOP block
        ItemBlockRenderTypes.setRenderLayer(SHOP.get(), RenderType.cutoutMipped());
    }
}