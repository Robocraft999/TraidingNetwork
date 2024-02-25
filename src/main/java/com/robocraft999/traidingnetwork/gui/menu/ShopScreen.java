package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShopScreen  extends AbstractContainerScreen<ShopMenu> {

    private static final ResourceLocation TEXTURE = TraidingNetwork.rl("textures/gui/shop.png");

    public ShopScreen(ShopMenu menu, Inventory playerInv, Component component) {
        super(menu, playerInv, component);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float v, int i, int i1) {
        renderBackground(graphics);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
