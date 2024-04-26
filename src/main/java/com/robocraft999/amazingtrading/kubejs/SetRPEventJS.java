package com.robocraft999.amazingtrading.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;

public class SetRPEventJS extends EventJS {
    public static SetRPEventJS INSTANCE = new SetRPEventJS();

    public void setRP(String str, long rp) {
        KubeJSRPMapperAfter.INSTANCE.items.put(str, rp);
    }

    /*public void setEMCAfter(String str, long emc) {
        KubeJSRPMapperAfter.INSTANCE.items.put(str, emc);
    }

    public void setEMCBefore(String str, long emc) {
        KubeJSRPMapperAfter.INSTANCE.items.put(str, emc);
    }*/
}
