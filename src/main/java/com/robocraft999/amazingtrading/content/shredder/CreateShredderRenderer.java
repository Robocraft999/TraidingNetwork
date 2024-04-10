package com.robocraft999.amazingtrading.content.shredder;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.robocraft999.amazingtrading.registry.ATPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CreateShredderRenderer extends KineticBlockEntityRenderer<CreateShredderBlockEntity> {

    public CreateShredderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CreateShredderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        boolean usingFlywheel = Backend.canUseInstancing(be.getLevel());
        if (usingFlywheel) return;

        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        BlockState blockState = be.getBlockState();

        PoseStack msLocal = new PoseStack();
        TransformStack msr = TransformStack.cast(msLocal);

        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();

        SuperByteBuffer shaftLeft = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, blockState, facing);
        shaftLeft.transform(msLocal);
        renderRotatingBuffer(be, shaftLeft, ms, builder, light);

        SuperByteBuffer shaftRight = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, blockState, facing.getOpposite());
        shaftRight.transform(msLocal);
        renderRotatingBuffer(be, shaftRight, ms, builder, light);

        SuperByteBuffer secondWheel = CachedBufferer.partialFacing(ATPartials.CRUSHER_COG, blockState, facing).light(light);
        msr.translate(0, 6f/16f, 0);
        msr.rotateCentered(Direction.UP, Mth.PI);
        secondWheel.transform(msLocal);
        renderRotatingBuffer(be, secondWheel, ms, builder, light);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CreateShredderBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(ATPartials.CRUSHER_COG, state, state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
    }
}
