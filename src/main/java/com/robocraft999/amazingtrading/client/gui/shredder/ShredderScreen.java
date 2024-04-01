package com.robocraft999.amazingtrading.client.gui.shredder;

import com.robocraft999.amazingtrading.TraidingNetwork;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.amazingtrading.utils.UIHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class ShredderScreen extends AbstractContainerScreen<ShredderMenu> {
    private static final ResourceLocation TEXTURE = TraidingNetwork.rl("textures/gui/shredder.png");
    private final ShredderInventory inv;
    public ShredderScreen(ShredderMenu menu, Inventory playerInv, Component component) {
        super(menu, playerInv, component);
        this.inv = menu.shredderInventory;
        this.imageWidth = 176;
        this.imageHeight = 132;
        this.titleLabelX = 4;
        this.titleLabelY = 4;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 40;
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
    protected void renderLabels(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xE4B763, false);

        BigInteger pointAmount = inv.provider.getPoints();
        //TODO add translation
        Component points = Component.literal("Points: " + pointAmount.toString());
        UIHelper.drawCenteredString(graphics, font, points, 88, 31, 0xE4B763, false);

        Level level = menu.getLevel();
        if (level != null && level.getBlockEntity(menu.blockPos) instanceof CreateShredderBlockEntity blockEntity && blockEntity != null){
            //TODO add translation
            var text = Component.literal("Owner: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(blockEntity.getOwnerName()));
            graphics.drawString(font, text, this.inventoryLabelX, 20, 0xE4B763, false);
        }
    }
}
