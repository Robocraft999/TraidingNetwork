package com.robocraft999.amazingtrading.content.shop;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ShopRenderer extends KineticBlockEntityRenderer<ShopBlockEntity> {
    public ShopRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ShopBlockEntity be, BlockState state) {
        return switch (state.getValue(ShopBlock.HALF)){
            case UPPER -> null;
            case LOWER -> CachedBufferer.partialFacing(AllPartialModels.SHAFTLESS_COGWHEEL, state).rotateToFace(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
        };
    }

    @Override
    protected void renderSafe(ShopBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        if (blockState.getValue(ShopBlock.HALF) == DoubleBlockHalf.UPPER)
            return;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        if (!Backend.canUseInstancing(be.getLevel())) {
            SuperByteBuffer superBuffer = CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);
            standardKineticRotationTransform(superBuffer, be, light);
            superBuffer.rotateCentered(Direction.UP, (float) (facing
                    .getAxis() != Direction.Axis.X ? 0 : Math.PI / 2));
            superBuffer.rotateCentered(Direction.EAST, (float) (Math.PI / 2));
            superBuffer.renderInto(ms, vb);
        }

        SuperByteBuffer superShaftBuffer = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, blockState, facing.getOpposite());
        renderRotatingBuffer(be, superShaftBuffer, ms, vb, light);
    }
}
