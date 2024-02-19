package com.robocraft999.traidingnetwork.block;

import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public interface ShredderBlock {

    class ShredderMenuProvider implements MenuProvider {

        @Override
        public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
            return new ShredderMenu(playerInventory, windowId);
        }

        @NotNull
        @Override
        public Component getDisplayName() {
            return Component.literal("test_name");//PELang.TRANSMUTATION_TRANSMUTE.translate();
        }
    }
}
