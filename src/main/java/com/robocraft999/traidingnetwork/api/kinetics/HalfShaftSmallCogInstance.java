package com.robocraft999.traidingnetwork.api.kinetics;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.crafter.ShaftlessCogwheelInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class HalfShaftSmallCogInstance extends ShaftlessCogwheelInstance {
    protected RotatingData shaft;
    public HalfShaftSmallCogInstance(MaterialManager materialManager, KineticBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();
        Direction facing = getRenderedBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        Instancer<RotatingData> half = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, facing);
        shaft = setup(half.createInstance().setRotationAxis(facing.getClockWise().getAxis()), getBlockEntitySpeed());
    }

    @Override
    public void update() {
        super.update();
        if (shaft != null) {
            updateRotation(shaft);
            shaft.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos));
        }
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (shaft != null)
            relight(pos, shaft);
    }

    @Override
    public void remove() {
        super.remove();
        if (shaft != null)
            shaft.delete();
    }
}
