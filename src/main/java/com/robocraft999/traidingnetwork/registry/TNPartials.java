package com.robocraft999.traidingnetwork.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.robocraft999.traidingnetwork.TraidingNetwork;

public class TNPartials {
    public static final PartialModel CRUSHER_COG = block("create_shredder/cog");

    private static PartialModel block(String path) {
        return new PartialModel(TraidingNetwork.rl("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
