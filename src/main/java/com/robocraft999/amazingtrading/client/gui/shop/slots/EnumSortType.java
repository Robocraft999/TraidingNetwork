package com.robocraft999.amazingtrading.client.gui.shop.slots;

public enum EnumSortType {

    AMOUNT, NAME, MOD;

    public EnumSortType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}