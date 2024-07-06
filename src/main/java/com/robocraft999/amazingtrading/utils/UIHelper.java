package com.robocraft999.amazingtrading.utils;

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

    public static void drawScaledString(@NotNull GuiGraphics graphics, Font font, Component text, int x, int y, int color, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }
}
