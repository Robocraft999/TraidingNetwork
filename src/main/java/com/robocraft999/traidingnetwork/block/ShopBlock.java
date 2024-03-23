package com.robocraft999.traidingnetwork.block;

import com.robocraft999.traidingnetwork.blockentity.ShopBlockEntity;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import com.robocraft999.traidingnetwork.registry.TNShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.stream.Stream;

public class ShopBlock extends HorizontalKineticBlock implements IBE<ShopBlockEntity>, ICogWheel {
    public ShopBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return TNShapes.SHOP.get(pState.getValue(HORIZONTAL_FACING));
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

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }
}
