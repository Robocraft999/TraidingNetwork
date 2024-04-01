package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.client.gui.shop.ShopMenu;
import com.robocraft999.amazingtrading.client.gui.shop.ShopScreen;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderMenu;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.robocraft999.amazingtrading.TraidingNetwork.REGISTRATE;

public class TNMenuTypes {

    public static final MenuEntry<ShredderMenu> SHREDDER_MENU = REGISTRATE
            .menu("shredder_menu", (menuType, i, inventory, data) -> new ShredderMenu(inventory, i, data.readBlockPos()), () -> ShredderScreen::new)
            .register();

    public static final MenuEntry<ShopMenu> SHOP_MENU = REGISTRATE
            .menu("shop_menu", (menuType, i, inventory, data) -> new ShopMenu(inventory, i, inventory.player.level(), data.readBlockPos()), () -> ShopScreen::new)
            .register();

    public static void register(){}
}
