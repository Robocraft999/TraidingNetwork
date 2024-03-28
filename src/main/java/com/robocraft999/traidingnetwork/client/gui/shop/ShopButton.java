package com.robocraft999.traidingnetwork.client.gui.shop;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShopButton extends Button {
    public static enum TextureEnum {

        SORT_AMT, SORT_MOD, SORT_NAME, SORT_UP, SORT_DOWN, RED, GREY;

        public int getX() {
            return switch (this) {
                case RED -> 21;
                case GREY -> 5;
                case SORT_NAME -> 32;
                case SORT_AMT -> 48;
                case SORT_MOD -> 64;
                case SORT_UP -> 16;
                case SORT_DOWN -> 0;
                default -> 0;
            };
        }

        public int getY() {
            return switch (this) {
                case RED, GREY -> 17;
                case SORT_UP, SORT_DOWN, SORT_AMT, SORT_MOD, SORT_NAME -> 0;
                default -> 0;
            };
        }
    }

    private static final int SIZE = 16;
    public ShopButton(int x, int y, String name, OnPress handler, CreateNarration narration) {
        super(x, y, SIZE, SIZE, Component.translatable(name), handler, narration);
        texture = new ResourceLocation(TraidingNetwork.MODID, "textures/gui/widgets.png");
    }

    public ShopButton setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    private ResourceLocation texture;
    private TextureEnum textureId = null;

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void render(GuiGraphics ms, int mx, int my, float pt) {
        super.render(ms, mx, my, pt);
    }

    private int getTextureY() {
        int i = 0;
        if (!this.active) {
            i = -10;
        }
        else if (this.isHoveredOrFocused()) {
            i = 1;
        }
        return width == SIZE && height == SIZE ? i : i + 2;// 46 + i * 20;
    }

    @Override
    public void renderWidget(GuiGraphics ms, int mouseX, int mouseY, float partial) {
        super.renderWidget(ms, mouseX, mouseY, partial);
        //    Minecraft minecraft = Minecraft.getInstance();
        //    minecraft.getTextureManager().bind(getTexture());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, getTexture());
        int k = getTextureY(); // getYImage ()
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        ms.blit(getTexture(), this.getX(), this.getY(), 0 + SIZE * k, 32, width, height);
        if (textureId != null) {
            ms.blit(getTexture(), this.getX(), this.getY(),
                    textureId.getX(), textureId.getY(),
                    width, height);
        }
        else {
            ms.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 2210752);
        }
    }

    public TextureEnum getTextureId() {
        return textureId;
    }

    public void setTextureId(TextureEnum textureId) {
        this.textureId = textureId;
    }
}
