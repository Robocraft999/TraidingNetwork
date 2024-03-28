package com.robocraft999.traidingnetwork.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class UIHelper {

    public static void drawCenteredString(@NotNull GuiGraphics graphics, Font font, Component component, int x, int y, int color, boolean dropShadow){
        FormattedCharSequence formattedcharsequence = component.getVisualOrderText();
        graphics.drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, dropShadow);
    }

    public static void drawCenteredString(@NotNull GuiGraphics graphics, Font font, Component component, int x, int y, int color){
        drawCenteredString(graphics, font, component, x, y, color, true);
    }
}
