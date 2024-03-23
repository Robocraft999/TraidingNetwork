package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.blockentity.*;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.robocraft999.traidingnetwork.TraidingNetwork.REGISTRATE;

public class TNBlockEntities {

    public static final BlockEntityEntry<CreateShredderBlockEntity> CREATE_SHREDDER = REGISTRATE
            .blockEntity("create_shredder", CreateShredderBlockEntity::new)
            .instance(() -> CreateShredderCogInstance::new)
            .validBlocks(TNBlocks.CREATE_SHREDDER)
            .renderer(() -> CreateShredderRenderer::new)
            .register();

    public static final BlockEntityEntry<ShopBlockEntity> SHOP = REGISTRATE
            .blockEntity("shop", ShopBlockEntity::new)
            .instance(() -> ShopCogInstance::new)
            .validBlocks(TNBlocks.SHOP)
            .renderer(() -> ShopRenderer::new)
            .register();



    public static void register(){}
}
