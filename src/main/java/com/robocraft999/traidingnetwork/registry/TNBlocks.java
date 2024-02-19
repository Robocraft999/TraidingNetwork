package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.block.CreateShredderBlock;
import com.robocraft999.traidingnetwork.block.ShopBlock;
import com.robocraft999.traidingnetwork.block.ShredderBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.robocraft999.traidingnetwork.TraidingNetwork.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class TNBlocks {
    public static final BlockEntry<? extends ShredderBlock> CREATE_SHREDDER = REGISTRATE
            .block("create_shredder", CreateShredderBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(BlockStressDefaults.setImpact(4))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<? extends ShopBlock> SHOP = REGISTRATE
            .block("shop", ShopBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(BlockStressDefaults.setNoImpact())
            .item()
            .transform(customItemModel())
            .register();

    public static void register(){}
}
