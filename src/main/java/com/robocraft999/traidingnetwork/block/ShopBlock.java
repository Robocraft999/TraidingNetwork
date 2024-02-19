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
            NetworkHooks.openScreen((ServerPlayer) player, new ShopMenuProvider(), b -> b.writeBoolean(false));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    static class ShopMenuProvider implements MenuProvider {

        @Override
        public Component getDisplayName() {
            return Component.literal("test_name2");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
            return new ShopMenu(playerInventory, i);
        }
    }
}
