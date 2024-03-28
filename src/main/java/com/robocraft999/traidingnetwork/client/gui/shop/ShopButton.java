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
            switch (this) {
                case RED:
                    return 179;
                case GREY:
                    return 197;
                case SORT_NAME:
                    return 198;
                case SORT_AMT:
                    return 209;
                case SORT_MOD:
                    return 221;
                case SORT_UP:
                    return 187;
                case SORT_DOWN:
                    return 175;
                default:
                    return 0;
            }
        }

        public int getY() {
            switch (this) {
                case RED:
                case GREY:
                    return 82;
                case SORT_UP:
                case SORT_DOWN:
                case SORT_AMT:
                case SORT_MOD:
                case SORT_NAME:
                    return 127;
                default:
                    return 0;
            }
        }
    }

    private static final int SIZE = 16;
    public ShopButton(int x, int y, String name, OnPress handler, CreateNarration narration) {
        super(x, y, SIZE, SIZE, Component.translatable(name), handler, narration);
        texture = new ResourceLocation(TraidingNetwork.MODID, "textures/gui/cable.png");
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
        int i = 1;
        if (!this.active) {
            i = 0;
        }
        else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return i;// 46 + i * 20;
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
        ms.blit(getTexture(), this.getX(), this.getY(),
                160 + SIZE * k, 52,
                width, height);
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
