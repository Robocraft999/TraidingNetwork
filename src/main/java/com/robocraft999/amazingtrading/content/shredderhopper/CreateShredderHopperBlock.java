package com.robocraft999.amazingtrading.content.shredderhopper;

import com.robocraft999.amazingtrading.api.kinetics.blockentity.IOwnedBlockEntity;
import com.robocraft999.amazingtrading.registry.ATBlockEntities;
import com.robocraft999.amazingtrading.registry.ATShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class CreateShredderHopperBlock extends HorizontalKineticBlock implements IBE<CreateShredderHopperBlockEntity> {

    protected UUID ownerId;

    public CreateShredderHopperBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return ATShapes.SHREDDER;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
    }

    @Override
    public Class<CreateShredderHopperBlockEntity> getBlockEntityClass() {
        return CreateShredderHopperBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateShredderHopperBlockEntity> getBlockEntityType() {
        return ATBlockEntities.CREATE_SHREDDER_HOPPER.get();
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (!worldIn.isClientSide && placer instanceof ServerPlayer player){
            ownerId = player.getUUID();
        }
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (!(blockEntity instanceof IOwnedBlockEntity obe))
            return;

        obe.setOwner(ownerId);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide){
            CreateShredderHopperBlockEntity blockEntity = getBlockEntity(level, pos);
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, b -> {
                b.writeBlockPos(pos);
                b.writeBoolean(false);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }
}
