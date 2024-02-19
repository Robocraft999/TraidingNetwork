package com.robocraft999.traidingnetwork.api.resourcepoints;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemNetwork implements INBTSerializable<CompoundTag> {

    public List<Slot> slots = new ArrayList<>();

    public static ItemNetwork INSTANCE = new ItemNetwork();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag properties = new CompoundTag();
        TraidingNetwork.LOGGER.info("TTT");
        return properties;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {

    }
}
