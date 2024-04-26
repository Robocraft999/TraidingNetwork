package com.robocraft999.amazingtrading.kubejs;

import com.robocraft999.amazingtrading.registry.ATCapabilities;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TagsUpdatedEvent;

import java.math.BigDecimal;

public class ATKubeJSPlugin extends KubeJSPlugin {
    public static EventGroup GROUP = EventGroup.of("ATEvents");
    public static EventHandler SET_RP = GROUP.server("mapping", () -> SetRPEventJS.class);

    @Override
    public void registerEvents() {
        GROUP.register();
    }

    public static void onServerReload(TagsUpdatedEvent event){
        SET_RP.post(ScriptType.SERVER, SetRPEventJS.INSTANCE);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("AmazingTrading", new ATUtils());
    }

    public static class ATUtils {
        public String getPlayerRP(Player player) {
            if (player == null) return null;
            var cap = player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY);
            if (cap.isPresent()) {
                return cap.resolve().get().getPoints().toString();
            }
            return "0";
        }

        public void setPlayerRP(Player player, String num) {
            if (player == null) return;
            player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.setPoints(new BigDecimal(num).toBigInteger());
            });
        }

        public void addPlayerRP(Player player, String num) {
            if (player == null) return;
            player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                cap.setPoints(cap.getPoints().add(new BigDecimal(num).toBigInteger()));
            });
        }
    }
}
