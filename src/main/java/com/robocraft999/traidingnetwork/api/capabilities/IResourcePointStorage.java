package com.robocraft999.traidingnetwork.api.capabilities;

import org.jetbrains.annotations.Range;

/**
 * inspired by https://github.com/sinkillerj/ProjectE/blob/mc1.20.x/src/api/java/moze_intel/projecte/api/capabilities/block_entity/IEmcStorage.java
 */
public interface IResourcePointStorage {

    enum RPAction {
        EXECUTE,
        SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }

        public RPAction combine(boolean execute) {
            return get(execute && execute());
        }

        public static RPAction get(boolean execute) {
            return execute ? EXECUTE : SIMULATE;
        }
    }

    @Range(from = 0, to = Long.MAX_VALUE)
    long getStoredPoints();

    @Range(from = 1, to = Long.MAX_VALUE)
    long getMaximumPoints();

    @Range(from = 0, to = Long.MAX_VALUE)
    default long getNeededPoints(){
        return Math.max(0, getMaximumPoints() - getStoredPoints());
    }

    default boolean hasMaxedPoints() {
        return getStoredPoints() >= getMaximumPoints();
    }

    long extractPoints(long toExtract, RPAction action);

    long insertPoints(long toAccept, RPAction action);
}
