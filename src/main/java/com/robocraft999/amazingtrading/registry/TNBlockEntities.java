package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.api.kinetics.HalfShaftSmallCogInstance;
import com.robocraft999.amazingtrading.content.shop.ShopBlockEntity;
import com.robocraft999.amazingtrading.content.shop.ShopRenderer;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderCogInstance;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.robocraft999.amazingtrading.TraidingNetwork.REGISTRATE;

public class TNBlockEntities {

    public static final BlockEntityEntry<CreateShredderBlockEntity> CREATE_SHREDDER = REGISTRATE
            .blockEntity("create_shredder", CreateShredderBlockEntity::new)
            .instance(() -> CreateShredderCogInstance::new)
            .validBlocks(TNBlocks.CREATE_SHREDDER)
            .renderer(() -> CreateShredderRenderer::new)
            .register();

    public static final BlockEntityEntry<ShopBlockEntity> SHOP = REGISTRATE
            .blockEntity("shop", ShopBlockEntity::new)
            .instance(() -> HalfShaftSmallCogInstance::new)
            .validBlocks(TNBlocks.SHOP)
            .renderer(() -> ShopRenderer::new)
            .register();



    public static void register(){}
}
