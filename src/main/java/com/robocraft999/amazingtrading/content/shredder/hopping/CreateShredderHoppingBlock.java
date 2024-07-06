package com.robocraft999.amazingtrading.content.shredder.hopping;

import com.robocraft999.amazingtrading.content.shredder.AbstractShredderBlock;
import com.robocraft999.amazingtrading.registry.ATBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateShredderHoppingBlock extends AbstractShredderBlock<CreateShredderHoppingBlockEntity> {

    public CreateShredderHoppingBlock(Properties properties) {
        super(properties);
    }


    @Override
    public Class<CreateShredderHoppingBlockEntity> getBlockEntityClass() {
        return CreateShredderHoppingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateShredderHoppingBlockEntity> getBlockEntityType() {
        return ATBlockEntities.CREATE_SHREDDER_HOPPING.get();
    }
}
