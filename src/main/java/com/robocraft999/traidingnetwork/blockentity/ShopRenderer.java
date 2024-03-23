package com.robocraft999.traidingnetwork.blockentity;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ShopRenderer extends KineticBlockEntityRenderer<ShopBlockEntity> {
    public ShopRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ShopBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFTLESS_COGWHEEL, state).rotateToFace(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
    }

    /*@Override
    protected void renderSafe(ShopBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;

        BlockState blockState = be.getBlockState();

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        SuperByteBuffer superBuffer = CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);//.rotateToFace(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
        standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);

    }*/
}
