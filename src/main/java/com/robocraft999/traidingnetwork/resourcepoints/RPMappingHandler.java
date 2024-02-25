package com.robocraft999.traidingnetwork.resourcepoints;

import com.robocraft999.traidingnetwork.api.ItemInfo;
import com.robocraft999.traidingnetwork.net.SyncResourcePointPKT.ResourcePointPKTInfo;

import java.util.HashMap;
import java.util.Map;

public class RPMappingHandler {
    private static final Map<ItemInfo, Long> points = new HashMap<>();

    public static void fromPacket(ResourcePointPKTInfo[] data) {
        points.clear();
        for (ResourcePointPKTInfo info : data) {
            points.put(ItemInfo.fromItem(info.item(), info.nbt()), info.points());
        }
    }
    public static ResourcePointPKTInfo[] createPacketData() {
        ResourcePointPKTInfo[] ret = new ResourcePointPKTInfo[points.size()];
        int i = 0;
        for (Map.Entry<ItemInfo, Long> entry : points.entrySet()) {
            ItemInfo info = entry.getKey();
            ret[i] = new ResourcePointPKTInfo(info.getItem(), info.getNBT(), entry.getValue());
            i++;
        }
        return ret;
    }
}
