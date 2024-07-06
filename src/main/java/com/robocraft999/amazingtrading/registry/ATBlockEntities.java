package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.api.kinetics.HalfShaftSmallCogInstance;
import com.robocraft999.amazingtrading.content.shop.ShopBlockEntity;
import com.robocraft999.amazingtrading.content.shop.ShopInstance;
import com.robocraft999.amazingtrading.content.shop.ShopRenderer;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderCogInstance;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderRenderer;
import com.robocraft999.amazingtrading.content.shredder.hopping.CreateShredderHoppingBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.robocraft999.amazingtrading.AmazingTrading.REGISTRATE;

public class ATBlockEntities {

    public static final BlockEntityEntry<CreateShredderBlockEntity> CREATE_SHREDDER = REGISTRATE
            .blockEntity("create_shredder", CreateShredderBlockEntity::new)
            .instance(() -> CreateShredderCogInstance::new)
            .validBlocks(ATBlocks.CREATE_SHREDDER)
            .renderer(() -> CreateShredderRenderer::new)
            .register();

    public static final BlockEntityEntry<CreateShredderHoppingBlockEntity> CREATE_SHREDDER_HOPPING = REGISTRATE
            .blockEntity("create_shredder_hopping", CreateShredderHoppingBlockEntity::new)
            .instance(() -> CreateShredderCogInstance::new)
            .validBlocks(ATBlocks.CREATE_SHREDDER_HOPPING)
            .renderer(() -> CreateShredderRenderer::new)
            .register();

    public static final BlockEntityEntry<ShopBlockEntity> SHOP = REGISTRATE
            .blockEntity("shop", ShopBlockEntity::new)
            .instance(() -> ShopInstance::new)
            .validBlocks(ATBlocks.SHOP)
            .renderer(() -> ShopRenderer::new)
            .register();

    public static void register(){}
}
