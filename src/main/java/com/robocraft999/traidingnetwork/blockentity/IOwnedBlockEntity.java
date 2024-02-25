package com.robocraft999.traidingnetwork.blockentity;

import java.util.UUID;

public interface IOwnedBlockEntity {
    UUID getOwnerId();

    void setOwner(UUID ownerId);
}
