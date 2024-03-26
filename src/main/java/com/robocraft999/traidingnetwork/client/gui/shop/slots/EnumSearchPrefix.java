package com.robocraft999.traidingnetwork.client.gui.shop.slots;

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
