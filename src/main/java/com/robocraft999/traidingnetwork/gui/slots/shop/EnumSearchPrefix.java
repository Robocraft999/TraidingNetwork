package com.robocraft999.traidingnetwork.gui.slots.shop;

public enum EnumSearchPrefix {
    MOD, TOOLTIP, TAG;

    public String getPrefix() {
        switch (this) {
            case MOD:
                return "@";
            case TAG:
                return "$";
            case TOOLTIP:
                return "#";
        }
        return "";
    }
}
