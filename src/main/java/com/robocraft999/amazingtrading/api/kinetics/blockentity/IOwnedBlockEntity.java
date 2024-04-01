package com.robocraft999.amazingtrading.api.kinetics.blockentity;

import java.util.UUID;

public interface IOwnedBlockEntity {
    UUID getOwnerId();

    void setOwner(UUID ownerId);
}
