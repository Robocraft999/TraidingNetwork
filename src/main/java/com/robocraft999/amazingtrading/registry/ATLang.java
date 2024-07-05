package com.robocraft999.amazingtrading.registry;

import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.client.gui.shop.slots.EnumSortType;

import static com.robocraft999.amazingtrading.AmazingTrading.REGISTRATE;

public class ATLang {

    public static final String KEY_GUI_DIRECTION_BUTTON = gui("direction");
    public static final String KEY_GUI_SORT_BUTTON_AMOUNT = gui("req.sort_amount");
    public static final String KEY_GUI_SORT_BUTTON_NAME = gui("req.sort_name");
    public static final String KEY_GUI_SORT_BUTTON_MOD = gui("req.sort_mod");
    public static final String KEY_GUI_AUTOFOCUS_BUTTON_TRUE = gui("autofocus.sort.true");
    public static final String KEY_GUI_AUTOFOCUS_BUTTON_FALSE = gui("autofocus.sort.false");
    public static final String KEY_GUI_SEARCH_TOOLTIP_MOD = gui("fil.tooltip_mod"); //@
    public static final String KEY_GUI_SEARCH_TOOLTIP_TOOLTIP = gui("fil.tooltip_tooltip"); //#
    public static final String KEY_GUI_SEARCH_TOOLTIP_TAGS = gui("fil.tooltip_tags"); //$
    public static final String KEY_GUI_SEARCH_TOOLTIP_CLEAR = gui("fil.tooltip_clear"); //clear
    public static final String KEY_GUI_SHIFT = gui("shift");

    public static final String KEY_SHREDDER_GUI_NAME = "gui." + AmazingTrading.MODID + ".shredder.name";
    public static final String KEY_SHREDDER_HOPPER_GUI_NAME = "gui." + AmazingTrading.MODID + ".shredderhopper.name";
    public static final String KEY_SHOP_GUI_NAME = "gui." + AmazingTrading.MODID + ".shop.name";

    public static final String NAME_CREATIVE_TAB = "amazing_trading";

    public static void init(){
        REGISTRATE.addRawLang(KEY_GUI_DIRECTION_BUTTON, "Sort Direction");
        REGISTRATE.addRawLang(KEY_GUI_SORT_BUTTON_AMOUNT, "Sort by amount");
        REGISTRATE.addRawLang(KEY_GUI_SORT_BUTTON_NAME, "Sort by name");
        REGISTRATE.addRawLang(KEY_GUI_SORT_BUTTON_MOD, "Sort by mod");
        REGISTRATE.addRawLang(KEY_GUI_AUTOFOCUS_BUTTON_TRUE, "Autofocus enabled");
        REGISTRATE.addRawLang(KEY_GUI_AUTOFOCUS_BUTTON_FALSE, "Autofocus disabled");
        REGISTRATE.addRawLang(KEY_GUI_SEARCH_TOOLTIP_MOD, "Prefix @: mod");
        REGISTRATE.addRawLang(KEY_GUI_SEARCH_TOOLTIP_TOOLTIP, "Prefix #: tooltip");
        REGISTRATE.addRawLang(KEY_GUI_SEARCH_TOOLTIP_TAGS, "Prefix $: tag");
        REGISTRATE.addRawLang(KEY_GUI_SEARCH_TOOLTIP_CLEAR, "Clear text with right-click");
        REGISTRATE.addRawLang(KEY_GUI_SHIFT, "Hold shift for more information");

        REGISTRATE.addRawLang(KEY_SHREDDER_GUI_NAME, "Shredder");
        REGISTRATE.addRawLang(KEY_SHREDDER_HOPPER_GUI_NAME, "Shredder Hopper");
        REGISTRATE.addRawLang(KEY_SHOP_GUI_NAME, "Shop");
    }

    private static String gui(String suffix){
        return "gui." + AmazingTrading.MODID + "." + suffix;
    }

    public static String sortButtonFromSortType(EnumSortType sortType){
        return switch (sortType){
            case AMOUNT -> KEY_GUI_SORT_BUTTON_AMOUNT;
            case NAME -> KEY_GUI_SORT_BUTTON_NAME;
            case MOD -> KEY_GUI_SORT_BUTTON_MOD;
        };
    }

    public static String autofocusButtonFromBoolean(boolean autofocus){
        return autofocus ? KEY_GUI_AUTOFOCUS_BUTTON_TRUE : KEY_GUI_AUTOFOCUS_BUTTON_FALSE;
    }
}
