package com.robocraft999.amazingtrading.content.shredder.hopping;

import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.amazingtrading.registry.ATLang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateShredderHoppingBlockEntity extends CreateShredderBlockEntity {

    public CreateShredderHoppingBlockEntity(BlockEntityType<? extends CreateShredderHoppingBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void onTick() {
        suckItems();
    }

    AABB area = new AABB(worldPosition).inflate(0.1, 1, 0.1);
    private void suckItems() {
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (inputInv.getStackInSlot(0).isEmpty() || inputInv.getStackInSlot(0).is(stack.getItem())) {
                ItemStack remaining = ItemHandlerHelper.insertItem(inputInv, stack, false);
                if (remaining.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
                if (!inputInv.getStackInSlot(0).isEmpty()) {
                    break;
                }
            }
        }
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable(ATLang.KEY_SHREDDER_HOPPING_GUI_NAME);
    }
}
