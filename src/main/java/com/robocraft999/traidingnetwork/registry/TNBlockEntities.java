package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.block.ShopBlock;
import com.robocraft999.traidingnetwork.blockentity.CreateShredderBlockEntity;
import com.robocraft999.traidingnetwork.blockentity.CreateShredderBlockEntityInstance;
import com.robocraft999.traidingnetwork.blockentity.ShopBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.robocraft999.traidingnetwork.TraidingNetwork.REGISTRATE;

public class TNBlockEntities {

    public static final BlockEntityEntry<CreateShredderBlockEntity> CREATE_SHREDDER = REGISTRATE
            .blockEntity("create_shredder", CreateShredderBlockEntity::new)
            .instance(() -> CreateShredderBlockEntityInstance::new)
            .validBlocks(TNBlocks.CREATE_SHREDDER)
            .register();

    public static final BlockEntityEntry<ShopBlockEntity> SHOP = REGISTRATE
            .blockEntity("shop", ShopBlockEntity::new)
            .validBlocks(TNBlocks.SHOP)
            .register();



    public static void register(){}
}
