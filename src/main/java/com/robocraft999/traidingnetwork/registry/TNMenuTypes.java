package com.robocraft999.traidingnetwork.registry;

import com.robocraft999.traidingnetwork.gui.menu.ShopMenu;
import com.robocraft999.traidingnetwork.gui.menu.ShopScreen;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.gui.menu.ShredderScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.robocraft999.traidingnetwork.TraidingNetwork.REGISTRATE;

public class TNMenuTypes {

    public static final MenuEntry<ShredderMenu> SHREDDER_MENU = REGISTRATE
            .menu("shredder_menu", (menuType, i, inventory) -> new ShredderMenu(inventory, i), () -> ShredderScreen::new)
            .register();

    public static final MenuEntry<ShopMenu> SHOP_MENU = REGISTRATE
            .menu("shop_menu", (menuType, i, inventory) -> new ShopMenu(inventory, i), () -> ShopScreen::new)
            .register();

    public static void register(){}
}
