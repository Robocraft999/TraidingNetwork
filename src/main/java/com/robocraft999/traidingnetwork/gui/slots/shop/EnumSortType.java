package com.robocraft999.traidingnetwork.gui.slots.shop;

public enum EnumSortType {

    AMOUNT, NAME, MOD;

    public EnumSortType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}