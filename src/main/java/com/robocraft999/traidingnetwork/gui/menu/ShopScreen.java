package com.robocraft999.traidingnetwork.gui.menu;

import com.mojang.blaze3d.platform.InputConstants;
import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.slots.shop.EnumSortType;
import com.robocraft999.traidingnetwork.api.capabilities.IShopNetworkSync;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.net.packets.shop.SyncSettingsPKT;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.registry.TNLang;
import com.robocraft999.traidingnetwork.resourcepoints.RItemStackHandler;
import com.robocraft999.traidingnetwork.utils.ItemHelper;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> implements IShopGui{

    final ShopWidget widget;

    private IShopNetworkSync provider;

    private static final ResourceLocation TEXTURE = TraidingNetwork.rl("textures/gui/inventory.png");

    public ShopScreen(ShopMenu menu, Inventory playerInv, Component component) {
        super(menu, playerInv, component);
        Player player = menu.shopInventory.player;
        this.provider = player.getCapability(TNCapabilities.SHOP_SETTINGS_CAPABILITY).orElseThrow(NullPointerException::new);
        this.widget = new ShopWidget(this);
        widget.setLines(8);
        imageWidth = 176;
        imageHeight = 256;
        widget.fieldHeight = 180;

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float v, int mouseX, int mouseY) {
        renderBackground(graphics);
        int xCenter = (width - imageWidth) / 2;
        int yCenter = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, xCenter, yCenter, 0, 0, imageWidth, imageHeight);

        widget.applySearchTextToSlots();
        widget.renderItemSlots(graphics, mouseX, mouseY, font);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        widget.drawGuiContainerForegroundLayer(graphics, mouseX, mouseY, font);

        AtomicReference<BigInteger> emcAmount = new AtomicReference<>(BigInteger.ONE);
        this.menu.shopInventory.player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
            emcAmount.set(cap.getPoints());
        });

        //Component emc = Component.literal(emcAmount.toString());
        int raw_amount = emcAmount.get().intValue();
        Component emc = Component.literal(raw_amount > 99999 ? ItemHelper.formatLargeNumber(raw_amount, false) : String.valueOf(raw_amount));
        //graphics.drawCenteredString(font, emc, 46, widget.searchBar.getY() - getGuiTop(), 0x404040);

        FormattedCharSequence formattedcharsequence = emc.getVisualOrderText();
        graphics.drawString(font, formattedcharsequence, 58 - font.width(formattedcharsequence) / 2, widget.searchBar.getY() - getGuiTop(), 0x404040, false);

        //graphics.drawString(font, emc, 38, widget.searchBar.getY() - getGuiTop(), 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
        widget.searchBar.render(graphics, mouseX, mouseY, partialTicks);
        widget.render();
    }

    boolean isScrollable(double x, double y) {
        int scrollHeight = 135;
        return isHovering(0, 0, this.width - 8, scrollHeight, x, y);
    }

    /**
     * Negative is down; positive is up.
     *
     * @param x
     * @param y
     * @param mouseButton
     * @return
     */
    @Override
    public boolean mouseScrolled(double x, double y, double mouseButton) {
        super.mouseScrolled(x, y, mouseButton);
        if (isScrollable(x, y) && mouseButton != 0) {
            widget.mouseScrolled(mouseButton);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        widget.mouseClicked(mouseX, mouseY, mouseButton);
        //recipe clear thingy
        /*int rectX = 63;
        int rectY = 110;
        if (isHovering(rectX, rectY, 7, 7, mouseX, mouseY)) {
            PacketRegistry.INSTANCE.sendToServer(new ClearRecipeMessage());
            PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
            return true;
        }*/
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int b) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (keyCode == 256) {
            minecraft.player.closeContainer();
            return true; // Forge MC-146650: Needs to return true when the key is handled.
        }
        if (widget.searchBar.isFocused()) {
            widget.searchBar.keyPressed(keyCode, scanCode, b);
            if (keyCode == 259) { // BACKSPACE

            }
            return true;
        }
        //regardles of above branch, also check this
        if (minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            minecraft.player.closeContainer();
            return true; // Forge MC-146650: Needs to return true when the key is handled.
        }
        return super.keyPressed(keyCode, scanCode, b);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (widget.charTyped(typedChar, keyCode)) {
            return true;
        }
        return false;
    }


    @Override
    protected void init() {
        super.init();
        int searchLeft = leftPos + 81, searchTop = getGuiTop() + 160, width = 85;
        widget.searchBar = new EditBox(font,
                searchLeft, searchTop,
                width, font.lineHeight, null);
        widget.searchBar.setMaxLength(30);
        widget.initSearchbar();
        widget.initButtons();
        addRenderableWidget(widget.directionBtn);
        addRenderableWidget(widget.sortBtn);
        addRenderableWidget(widget.focusBtn);
        if (this.getAutoFocus()) {
            widget.searchBar.setFocused(true);
        }
    }

    @Override
    public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void drawGradient(GuiGraphics graphics, int x, int y, int x2, int y2, int u, int v) {
        graphics.fillGradient(x, y, x2, y2, u, v);
    }

    @Override
    public void renderStackTooltip(GuiGraphics graphics, ItemStack stack, int i, int j) {
        //List<Component> tooltip = stack.getTooltipLines(minecraft.player, TooltipFlag.NORMAL);
        List<Component> tooltip = ShopScreen.getTooltipFromItem(minecraft, stack);
        /*if (!tooltip.get(tooltip.size() - 1).getString().isEmpty()){
            tooltip.add(tooltip.size() - 1, Component.empty());
        }*/
        if (!Screen.hasShiftDown()){
            tooltip.add(Component.translatable(TNLang.KEY_GUI_SHIFT).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.literal("Cost: " + ResourcePointHelper.getResourcePointValue(stack)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            tooltip.add(Component.literal("Amount: " + stack.getCount()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }


        //stack.getItem().appendHoverText(stack, minecraft.level, tooltip, TooltipFlag.NORMAL);

        graphics.renderTooltip(font, tooltip, stack.getTooltipImage(), i, j);
        //graphics.renderTooltip(font, stack, i, j);
    }

    @Override
    public void setStacks(List<ItemStack> stacks) {
        widget.stacks = stacks;
    }

    @Override
    public List<ItemStack> getStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        minecraft.level.getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(cap -> {
            RItemStackHandler handler = (RItemStackHandler) cap.getSlotsHandler();
            stacks.addAll(handler.getItems());
            //TraidingNetwork.LOGGER.info("stacks1: " +stacks);
            stacks.removeIf(ItemStack::isEmpty);
            /*IItemHandler handler = cap.getSlotsHandler();
            for (int i = 0; i < handler.getSlots(); i++){
                ItemStack stackInSlot = handler.getStackInSlot(i);
                if (!stackInSlot.isEmpty())
                    stacks.add(stackInSlot);
            }*/
        });

        //TraidingNetwork.LOGGER.info("stacks: " +stacks);

        return stacks;
    }

    @Override
    public boolean getDownwards() {
        return provider.isDownwards();
    }

    @Override
    public void setDownwards(boolean val) {
        TraidingNetwork.LOGGER.info("Downwards new: " + val);
        provider.setDownwards(val);
    }

    @Override
    public EnumSortType getSort() {
        return provider.getSort();
    }

    @Override
    public void setSort(EnumSortType val) {
        TraidingNetwork.LOGGER.info("Sort new: " + val.name());
        provider.setSort(val);
    }

    @Override
    public void syncDataToServer() {
        PacketHandler.sendToServer(new SyncSettingsPKT(getDownwards(), getSort(), getAutoFocus()));
    }

    @Override
    public boolean getAutoFocus() {
        return provider.getAutoFocus();
    }

    @Override
    public void setAutoFocus(boolean b) {
        provider.setAutoFocus(b);
    }
}
