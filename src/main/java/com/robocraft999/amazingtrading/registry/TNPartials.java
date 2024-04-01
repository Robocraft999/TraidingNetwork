package com.robocraft999.amazingtrading.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.robocraft999.amazingtrading.AmazingTrading;

public class TNPartials {
    public static final PartialModel CRUSHER_COG = block("create_shredder/cog");

    private static PartialModel block(String path) {
        return new PartialModel(AmazingTrading.rl("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
