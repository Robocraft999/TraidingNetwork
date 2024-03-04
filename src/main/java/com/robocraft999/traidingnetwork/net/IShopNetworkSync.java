package com.robocraft999.traidingnetwork.net;

import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;

public interface IShopNetworkSync {
    boolean isDownwards();

    void setDownwards(boolean downwards);

    EnumSortType getSort();

    void setSort(EnumSortType sort);

    void setAutoFocus(boolean autoFocus);
}
