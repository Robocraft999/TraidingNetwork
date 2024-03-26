package com.robocraft999.traidingnetwork.client.gui.shop.slots;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ShopButton extends Button {
    private static final int SIZE = 16;
    public ShopButton(int x, int y, String name, OnPress handler, CreateNarration narration) {
        super(x, y, SIZE, SIZE, Component.translatable(name), handler, narration);
    }
}
