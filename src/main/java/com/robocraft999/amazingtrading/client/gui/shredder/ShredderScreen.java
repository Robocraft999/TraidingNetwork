package com.robocraft999.amazingtrading.client.gui.shredder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.content.shredder.CreateShredderBlockEntity;
import com.robocraft999.amazingtrading.utils.UIHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class ShredderScreen extends AbstractContainerScreen<ShredderMenu> {
    private static final ResourceLocation TEXTURE = AmazingTrading.rl("textures/gui/shredder.png");
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
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Render the item being processed in the shredder
        renderProcessingItem(graphics, x, y);
    }

    private void renderProcessingItem(@NotNull GuiGraphics graphics, int x, int y) {
        Level level = menu.getLevel();
        if (level != null && level.getBlockEntity(menu.blockPos) instanceof CreateShredderBlockEntity blockEntity) {
            ItemStack stackInSlot = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(handler -> handler.getStackInSlot(0))
                    .orElse(ItemStack.EMPTY);
            if (!stackInSlot.isEmpty()) {
                // Adjust the position as needed
                graphics.renderItem(stackInSlot, x + 152, y + 32);
                graphics.renderItemDecorations(this.font, stackInSlot, x + 120, y + 20); // Render item count
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xE4B763, false);

        BigInteger pointAmount = inv.provider.getPoints();
        // TODO: Add translation
        Component points = Component.literal("Your Points: " + pointAmount.toString());
        UIHelper.drawCenteredString(graphics, font, points, 88, 31, 0xE4B763, false);

        Level level = menu.getLevel();
        if (level != null && level.getBlockEntity(menu.blockPos) instanceof CreateShredderBlockEntity blockEntity) {
            // TODO: Add translation
            var text = Component.literal("Owner: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(blockEntity.getOwnerName()));
            graphics.drawString(font, text, this.inventoryLabelX, 20, 0xE4B763, false);
        }
    }
}