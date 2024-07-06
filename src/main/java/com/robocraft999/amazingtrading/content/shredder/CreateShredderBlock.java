package com.robocraft999.amazingtrading.content.shredder;

import com.robocraft999.amazingtrading.registry.ATBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateShredderBlock extends AbstractShredderBlock<CreateShredderBlockEntity> {

    public CreateShredderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CreateShredderBlockEntity> getBlockEntityClass() {
        return CreateShredderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateShredderBlockEntity> getBlockEntityType() {
        return ATBlockEntities.CREATE_SHREDDER.get();
    }
}
