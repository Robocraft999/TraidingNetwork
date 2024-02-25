package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class ShredderScreen extends AbstractContainerScreen<ShredderMenu> {
    private static final ResourceLocation TEXTURE = TraidingNetwork.rl("textures/gui/shredder.png");
    private final ShredderInventory inv;
    public ShredderScreen(ShredderMenu menu, Inventory playerInv, Component component) {
        super(menu, playerInv, component);
        this.inv = menu.shredderInventory;
        this.imageWidth = 228;
        this.imageHeight = 196;
        this.titleLabelX = 6;
        this.titleLabelY = 8;
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

    @Override
    protected void renderLabels(GuiGraphics graphics, int p_282681_, int p_283686_) {
        //super.renderLabels(p_281635_, p_282681_, p_283686_);
        BigInteger emcAmount = inv.provider.getPoints();
        //graphics.drawString(font, PELang.EMC_TOOLTIP.translate(""), 6, this.imageHeight - 104, 0x404040, false);
        Component emc = Component.literal(emcAmount.toString());
        graphics.drawString(font, emc, 6, this.imageHeight - 94, 0x404040, false);
    }
}
