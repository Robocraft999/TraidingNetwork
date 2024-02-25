package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.blockentity.CreateShredderBlockEntity;
import com.robocraft999.traidingnetwork.blockentity.CreateShredderCogInstance;
import com.robocraft999.traidingnetwork.blockentity.CreateShredderRenderer;
import com.robocraft999.traidingnetwork.blockentity.ShopBlockEntity;
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
            .validBlocks(TNBlocks.SHOP)
            .register();



    public static void register(){}
}
