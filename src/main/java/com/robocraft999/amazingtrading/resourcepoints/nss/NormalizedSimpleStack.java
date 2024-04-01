package com.robocraft999.amazingtrading.resourcepoints.nss;

/**
 * Represents a "stack" to be used by the RP mapper.
 */
public interface NormalizedSimpleStack {

    /**
     * @return JSON representation of this {@link NormalizedSimpleStack} for use in serialization.
     */
    String json();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();
}
