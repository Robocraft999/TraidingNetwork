package com.robocraft999.amazingtrading.client.gui.shop.slots;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.robocraft999.amazingtrading.client.gui.menu.IShopGui;
import com.robocraft999.amazingtrading.utils.ItemHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ItemSlotNetwork {

    private final int x;
    private final int y;
    private int size;
    private final int guiLeft;
    private final int guiTop;
    private boolean showNumbers;
    private final IShopGui parent;
    private ItemStack stack;

    public ItemSlotNetwork(IShopGui parent, ItemStack stack, int x, int y, int size, int guiLeft, int guiTop, boolean number) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        setShowNumbers(number);
        this.parent = parent;
        setStack(stack);
    }

    public boolean isMouseOverSlot(int mouseX, int mouseY) {
        return parent.isInRegion(x - guiLeft, y - guiTop, 16, 16, mouseX, mouseY);
    }

    public void drawSlot(GuiGraphics poseStack, Font font, int mx, int my, boolean selected) {
        if (!getStack().isEmpty()) {
            //      poseStack.pushPose();
            String amount;
            //cant sneak in gui
            //default to short form, show full amount if sneak
            if (false && Screen.hasShiftDown()) {
                amount = size + "";
            }
            else {
                amount = ItemHelper.formatLargeNumber(size);
            }

            //final float scale = 0.85F;
            boolean p = (Screen.hasShiftDown() && amount.length() > 3) || amount.length() > 4;
            p = amount.length() > 4;
            float scale = Mth.lerp(p ? -0.08F*(amount.length()-1)+1 : 1, 0.45F, 0.85F);
            scale = p ? 0.75F : 0.85F;
            //TraidingNetwork.LOGGER.info(""+scale);
            float offset = Mth.lerp(p ? -0.2F*(amount.length()-1)+1 : 1, 3, 0);
            offset = p ? 2 : 0;
            PoseStack viewModelPose = RenderSystem.getModelViewStack();
            viewModelPose.pushPose();
            //viewModelPose.translate(x + 3 + offset, y + 3 + 1.5*offset, 0);
            viewModelPose.translate(x + 3 + offset, y + 3 + 0.8*offset, 0);
            viewModelPose.scale(scale, scale, scale);
            viewModelPose.translate(-1 * x, -1 * y, 0);
            RenderSystem.applyModelViewMatrix();
            if (isShowNumbers() && size > 1) {
                poseStack.renderItemDecorations(font, stack, x, y, amount);
            }
            viewModelPose.popPose();
            RenderSystem.applyModelViewMatrix();
            if (isMouseOverSlot(mx, my)) {
                int j1 = x;
                int k1 = y;
                RenderSystem.colorMask(true, true, true, false);
                poseStack.fillGradient(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                //poseStack.renderOutline(j1, k1, 16, 16, -2130706433);
                //        parent.drawGradient(poseStack, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
            }

            if (selected) {
                int j1 = x;
                int k1 = y;
                RenderSystem.colorMask(true, true, true, false);
                poseStack.renderOutline(j1, k1, 16, 16, 0xFFf7cb6c);
                RenderSystem.colorMask(true, true, true, true);
            }
            poseStack.renderItem(stack, x, y);
            //      Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(poseStack, getStack(), x, y);
        }
    }

    public void drawTooltip(GuiGraphics ms, int mx, int my) {
        if (isMouseOverSlot(mx, my) && !getStack().isEmpty()) {
            parent.renderStackTooltip(ms, getStack(),
                    mx - parent.getGuiLeft(),
                    my - parent.getGuiTop());
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private boolean isShowNumbers() {
        return showNumbers;
    }

    private void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }
}
