package com.robocraft999.traidingnetwork.block;

import com.robocraft999.traidingnetwork.blockentity.CreateShredderBlockEntity;
import com.robocraft999.traidingnetwork.registry.TNBlockEntities;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class CreateShredderBlock extends HorizontalKineticBlock implements ShredderBlock, IBE<CreateShredderBlockEntity> {

    public CreateShredderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<CreateShredderBlockEntity> getBlockEntityClass() {
        return CreateShredderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateShredderBlockEntity> getBlockEntityType() {
        return TNBlockEntities.CREATE_SHREDDER.get();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide){
            NetworkHooks.openScreen((ServerPlayer) player, new ShredderMenuProvider(), b -> b.writeBoolean(false));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }
}
