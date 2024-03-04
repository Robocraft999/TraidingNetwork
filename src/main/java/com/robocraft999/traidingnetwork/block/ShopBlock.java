package com.robocraft999.traidingnetwork.block;

import com.robocraft999.traidingnetwork.blockentity.ShopBlockEntity;
import com.robocraft999.traidingnetwork.gui.menu.ShopMenu;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ShopBlock extends Block implements IBE<ShopBlockEntity> {
    public ShopBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<ShopBlockEntity> getBlockEntityClass() {
        return ShopBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ShopBlockEntity> getBlockEntityType() {
        return TNBlockEntities.SHOP.get();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide){
            ShopBlockEntity blockEntity = getBlockEntity(level, pos);
            //TODO sync settings
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, b -> {
                b.writeBlockPos(pos);
                b.writeBoolean(false);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
