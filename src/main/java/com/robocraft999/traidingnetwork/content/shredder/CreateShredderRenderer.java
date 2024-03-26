package com.robocraft999.traidingnetwork.content.shredder;

import com.robocraft999.traidingnetwork.registry.TNPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class CreateShredderRenderer extends KineticBlockEntityRenderer<CreateShredderBlockEntity> {

    public CreateShredderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CreateShredderBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(TNPartials.CRUSHER_COG, state);
    }
}
