package com.robocraft999.traidingnetwork.registry;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class TNShapes {
    public static final VoxelShape
        SHREDDER = shape(0,0,0,16,5,16).add(2,0,2,14,16,14).build();
    public static final VoxelShaper
        SHOP = shape(Stream.of(
                Block.box(0, 0, 0, 16, 16, 16),
                Block.box(0, 16, 7, 16, 32, 16),
                Block.box(0, 16, 2, 16, 17, 7),
                Block.box(0, 17, 3, 16, 18, 7),
                Block.box(0, 18, 4, 16, 19, 7),
                Block.box(0, 19, 5, 16, 20, 7),
                Block.box(0, 20, 6, 16, 21, 7)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()).forDirectional(Direction.NORTH);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
