package com.robocraft999.amazingtrading.content.shop;

import com.jozufozu.flywheel.api.MaterialManager;
import com.robocraft999.amazingtrading.api.kinetics.HalfShaftSmallCogInstance;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ShopInstance extends HalfShaftSmallCogInstance {
    private final boolean isBase;
    public ShopInstance(MaterialManager materialManager, ShopBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        isBase = blockEntity.getBlockState().getValue(ShopBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public void init() {
        if (isBase)
            super.init();
    }

    @Override
    public void updateLight() {
        if (isBase)
            super.updateLight();
    }

    @Override
    public void update() {
        if (isBase)
            super.update();
    }

    @Override
    public void remove() {
        if (isBase)
            super.remove();
    }
}
