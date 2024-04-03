package com.robocraft999.amazingtrading.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public interface IResourcePointProvider extends INBTSerializable<CompoundTag> {

    enum TargetUpdateType {
        /**
         * Don't update targets.
         */
        NONE,
        /**
         * Only update if "needed", the points value is below the highest item.
         */
        IF_NEEDED,
        /**
         * Update targets.
         */
        ALL
    }

    BigInteger getPoints();

    void setPoints(BigInteger points);

    void syncPoints(@NotNull ServerPlayer player);

    void sync(@NotNull ServerPlayer player);

    boolean isSecretEnabled();

    void enableSecret();
}
