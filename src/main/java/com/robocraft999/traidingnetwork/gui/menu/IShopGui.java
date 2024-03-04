package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IShopGui {
    int getGuiTop();

    int getGuiLeft();

    boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY);

    void drawGradient(GuiGraphics ms, int j1, int k1, int i, int j, int k, int l);

    void renderStackTooltip(GuiGraphics ms, ItemStack stack, int i, int j);

    void setStacks(List<ItemStack> stacks);

    List<ItemStack> getStacks();

    boolean getDownwards();

    void setDownwards(boolean val);

    EnumSortType getSort();

    void syncDataToServer();

    void setSort(EnumSortType val);

    boolean getAutoFocus();

    void setAutoFocus(boolean b);
}
