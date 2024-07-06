package com.robocraft999.amazingtrading.content.shredderhopper;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.robocraft999.amazingtrading.registry.ATPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CreateShredderHopperCogInstance extends KineticBlockEntityInstance<CreateShredderHopperBlockEntity> implements DynamicInstance {
    protected RotatingData rotatingModel1;
    protected RotatingData rotatingModel2;
    protected RotatingData shaft;

    public CreateShredderHopperCogInstance(MaterialManager materialManager, CreateShredderHopperBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        this.rotatingModel1 = this.setup(this.getModel().createInstance());
        this.rotatingModel2 = this.setup(this.getModel().createInstance());
        this.shaft = this.setup(this.getShaftModel().createInstance());

        rotatingModel1.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        rotatingModel2.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                //.setColor(blockEntity)
                .setPosition(getInstancePosition())
                .nudge(0, 6f/16f, 0)
                .setRotationalSpeed(-getBlockEntitySpeed());
    }

    @Override
    public void update() {
        this.updateRotation(this.rotatingModel1);
        this.updateRotation(this.rotatingModel2);
        this.updateRotation(this.shaft);
        rotatingModel2.setRotationalSpeed(-getBlockEntitySpeed());
    }

    @Override
    public void updateLight() {
        this.relight(this.pos.above(), this.rotatingModel1, this.rotatingModel2, this.shaft);
    }

    @Override
    public void remove() {
        this.rotatingModel1.delete();
        this.rotatingModel2.delete();
        this.shaft.delete();
    }

    protected BlockState getShaftRenderedBlockState() {
        return shaft(blockState.getValue(CreateShredderHopperBlock.HORIZONTAL_FACING).getClockWise().getAxis());
    }

    protected Instancer<RotatingData> getShaftModel() {
        return this.getRotatingMaterial().getModel(this.getShaftRenderedBlockState());
    }

    protected Instancer<RotatingData> getModel() {
        BlockState referenceState = blockEntity.getBlockState();
        Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
        return getRotatingMaterial().getModel(ATPartials.CRUSHER_COG, referenceState, facing);
    }

    @Override
    public void beginFrame() {
    }
}
