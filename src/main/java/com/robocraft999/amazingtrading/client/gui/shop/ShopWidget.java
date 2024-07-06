package com.robocraft999.amazingtrading.client.gui.shop;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.robocraft999.amazingtrading.client.gui.menu.IShopGui;
import com.robocraft999.amazingtrading.client.gui.shop.ShopButton.TextureEnum;
import com.robocraft999.amazingtrading.client.gui.shop.slots.EnumSearchPrefix;
import com.robocraft999.amazingtrading.client.gui.shop.slots.ItemSlotNetwork;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.net.packets.shop.ShopRequestPKT;
import com.robocraft999.amazingtrading.registry.ATLang;
import com.robocraft999.amazingtrading.utils.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.*;
import java.util.stream.Collectors;

public class ShopWidget {
    protected static final Button.CreateNarration DEFAULT_NARRATION = (supplier) -> {
        return supplier.get();
    };

    public ItemStack stackUnderMouse = ItemStack.EMPTY;
    public Item selected = Items.AIR;
    public List<ItemStack> stacks;
    public EditBox searchBar;
    public ShopButton directionBtn;
    public ShopButton sortBtn;
    public ShopButton focusBtn;
    public int fieldHeight = 90;
    private List<ItemSlotNetwork> slots;
    private final IShopGui gui;
    private long lastClick;
    private int page = 1;
    private int maxPage = 1;
    private int lines = 4;
    private final int columns = 9;

    public static final int MOUSE_BTN_LEFT = 0;
    public static final int MOUSE_BTN_RIGHT = 1;


    public ShopWidget(IShopGui gui){
        this.gui = gui;
        stacks = Lists.newArrayList();
        slots = Lists.newArrayList();
        lastClick = System.currentTimeMillis();
    }

    public void applySearchTextToSlots() {
        String searchText = searchBar.getValue();
        stacks = gui.getStacks();
        //AmazingTrading.LOGGER.info("apsts stacks: "+stacks);
        List<ItemStack> stacksToDisplay = searchText.isEmpty() ? Lists.newArrayList(stacks) : Lists.newArrayList();
        if (!searchText.isEmpty()) {
            for (ItemStack stack : stacks) {
                if (doesStackMatchSearch(stack)) {
                    stacksToDisplay.add(stack);
                }
            }
        }
        this.sortStackWrappers(stacksToDisplay);
        //AmazingTrading.LOGGER.info("apsts stackstodisplay: "+stacksToDisplay);
        this.applyScrollPaging(stacksToDisplay);
        this.rebuildItemSlots(stacksToDisplay);
    }

    public void clearSearch() {
        if (searchBar == null) {
            return;
        }
        searchBar.setValue("");
    }

    private boolean doesStackMatchSearch(ItemStack stack) {
        String searchText = searchBar.getValue();
        if (searchText.startsWith(EnumSearchPrefix.MOD.getPrefix())) { //  search modname
            String name = ItemHelper.getModNameForItem(stack.getItem());
            return name.toLowerCase().contains(searchText.toLowerCase().substring(1));
        }
        else if (searchText.startsWith(EnumSearchPrefix.TOOLTIP.getPrefix())) { // search tooltips
            String tooltipString;
            Minecraft mc = Minecraft.getInstance();
            List<Component> tooltip = stack.getTooltipLines(mc.player, TooltipFlag.Default.NORMAL);
            List<String> unformattedTooltip = tooltip.stream().map(Component::getString).collect(Collectors.toList());
            tooltipString = Joiner.on(' ').join(unformattedTooltip).toLowerCase().trim();
            return tooltipString.contains(searchText.toLowerCase().substring(1));
        }
        else if (searchText.startsWith(EnumSearchPrefix.TAG.getPrefix())) { // search tags
            List<String> joiner = new ArrayList<>();
            for (ResourceLocation oreId : stack.getTags().map(TagKey::location).toList()) {
                String oreName = oreId.toString();
                joiner.add(oreName);
            }
            String dictFinal = Joiner.on(' ').join(joiner).toLowerCase().trim();
            return dictFinal.contains(searchText.toLowerCase().substring(1));
        }
        else {
            return stack.getHoverName().getString().toLowerCase().contains(searchText.toLowerCase());
        }
    }

    public boolean canClick() {
        return System.currentTimeMillis() > lastClick + 100L;
    }

    int getLines() {
        return lines;
    }

    int getColumns() {
        return columns;
    }

    public void setLines(int v) {
        lines = v;
    }

    public void applyScrollPaging(List<ItemStack> stacksToDisplay) {
        maxPage = stacksToDisplay.size() / (getColumns());
        if (stacksToDisplay.size() % (getColumns()) != 0) {
            maxPage++;
        }
        maxPage -= (getLines() - 1);
        if (maxPage < 1) {
            maxPage = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > maxPage) {
            page = maxPage;
        }
    }

    public void mouseScrolled(double mouseButton) {
        // < 0 going down
        // > 0 going up
        if (mouseButton > 0 && page > 1) {
            page--;
        }
        if (mouseButton < 0 && page < maxPage) {
            page++;
        }
        //AmazingTrading.LOGGER.info("mouse scroll dir: "+ mouseButton + " page: " + page);
    }

    public void rebuildItemSlots(List<ItemStack> stacksToDisplay) {
        slots = Lists.newArrayList();
        int index = (page - 1) * (getColumns());
        for (int row = 0; row < getLines(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                if (index >= stacksToDisplay.size()) {
                    break;
                }
                //AmazingTrading.LOGGER.info("row: "+row+" col: "+col+" :" + stacksToDisplay.get(index) + " sort: " + gui.getSort() + " " + gui.getDownwards());
                int in = index;
                if (stacksToDisplay.get(in).isEmpty()){
                    continue;
                }
                //AmazingTrading.LOGGER.info("row: "+row+" col: "+col+" :" + stacksToDisplay.get(index) + " sort: " + gui.getSort() + " " + gui.getDownwards());
                //        StorageNetwork.LOGGER.info(in + "GUI STORAGE rebuildItemSlots "+stacksToDisplay.get(in));
                slots.add(new ItemSlotNetwork(gui, stacksToDisplay.get(in),
                        gui.getGuiLeft() + 8 + col * 18,
                        gui.getGuiTop() + 12 + row * 18,
                        stacksToDisplay.get(in).getCount(),
                        gui.getGuiLeft(), gui.getGuiTop(), true));
                index++;
            }
        }
    }

    public boolean inSearchBar(double mouseX, double mouseY) {
        return gui.isInRegion(
                searchBar.getX() - gui.getGuiLeft(), searchBar.getY() - gui.getGuiTop(),
                searchBar.getWidth(), searchBar.getHeight(),
                mouseX, mouseY);
    }

    public void initSearchbar() {
        searchBar.setBordered(false);
        searchBar.setVisible(true);
        searchBar.setTextColor(0xEEC168);
    }

    public void initButtons() {
        int y = this.searchBar.getY() - 4;
        directionBtn = new ShopButton(gui.getGuiLeft() + 7, y, "", (p) -> {
            gui.setDownwards(!gui.getDownwards());
            gui.syncDataToServer();
        }, DEFAULT_NARRATION);
        directionBtn.setHeight(16);
        sortBtn = new ShopButton(gui.getGuiLeft() + 24, y, "", (p) -> {
            gui.setSort(gui.getSort().next());
            gui.syncDataToServer();
        }, DEFAULT_NARRATION);
        sortBtn.setHeight(16);
        focusBtn = new ShopButton(
                gui.getGuiLeft() + 163, y + 1, "", (p) -> {
            gui.setAutoFocus(!gui.getAutoFocus());
            gui.syncDataToServer();
        }, DEFAULT_NARRATION);
        focusBtn.setHeight(14);
        focusBtn.setWidth(6);
    }

    public void drawGuiContainerForegroundLayer(GuiGraphics ms, int mouseX, int mouseY, Font font) {
        for (ItemSlotNetwork slot : slots) {
            if (slot != null && slot.isMouseOverSlot(mouseX, mouseY)) {
                slot.drawTooltip(ms, mouseX, mouseY);
                return; // slots and btns do not overlap
            }
        }
        //
        MutableComponent tooltip = null;
        if (directionBtn != null && directionBtn.isMouseOver(mouseX, mouseY)) {
            tooltip = Component.translatable(ATLang.KEY_GUI_DIRECTION_BUTTON);
        }
        else if (sortBtn != null && sortBtn.isMouseOver(mouseX, mouseY)) {
            tooltip = Component.translatable(ATLang.sortButtonFromSortType(gui.getSort()));
        }
        else if (focusBtn != null && focusBtn.isMouseOver(mouseX, mouseY)) {
            tooltip = Component.translatable(ATLang.autofocusButtonFromBoolean(gui.getAutoFocus()));
        }
        else if (this.inSearchBar(mouseX, mouseY)) {
            //tooltip = new TranslationTextComponent("gui.storagenetwork.fil.tooltip_clear");
            if (!Screen.hasShiftDown()) {
                tooltip = Component.translatable(ATLang.KEY_GUI_SHIFT);
            }
            else {
                List<Component> lis = Lists.newArrayList();
                lis.add(Component.translatable(ATLang.KEY_GUI_SEARCH_TOOLTIP_MOD)); //@
                lis.add(Component.translatable(ATLang.KEY_GUI_SEARCH_TOOLTIP_TOOLTIP)); //#
                lis.add(Component.translatable(ATLang.KEY_GUI_SEARCH_TOOLTIP_TAGS)); //$
                //lis.add(Component.translatable(TNLang.KEY_GUI_SEARCH_TOOLTIP_CLEAR)); //clear
                //AmazingTrading.LOGGER.debug(lis.toString());
                //        Screen screen = ((Screen) gui);
                ms.renderTooltip(font, lis, Optional.empty(), mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
                return; // all done, we have our tts rendered
            }
        }
        //do we have a tooltip
        if (tooltip != null) {
            //      Screen screen = ((Screen) gui);
            ms.renderTooltip(font, Lists.newArrayList(tooltip), Optional.empty(), mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
        }
    }

    public void renderItemSlots(GuiGraphics ms, int mouseX, int mouseY, Font font) {
        stackUnderMouse = ItemStack.EMPTY;
        for (ItemSlotNetwork slot : slots) {
            slot.drawSlot(ms, font, mouseX, mouseY, slot.getStack().getItem() == selected);
            if (slot.isMouseOverSlot(mouseX, mouseY)) {
                stackUnderMouse = slot.getStack();
            }
        }
        if (slots.isEmpty()) {
            stackUnderMouse = ItemStack.EMPTY;
        }
    }

    public boolean charTyped(char typedChar, int keyCode) {
        if (searchBar.isFocused() && searchBar.charTyped(typedChar, keyCode)) {
            //PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
            return true;
        }
        return false;
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        searchBar.setFocused(false);
        if (inSearchBar(mouseX, mouseY)) {
            searchBar.setFocused(true);
            if (mouseButton == MOUSE_BTN_RIGHT) {
                clearSearch();
                return;
            }
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stackCarriedByMouse = player.containerMenu.getCarried();

        if (!stackUnderMouse.isEmpty()
                && (mouseButton == MOUSE_BTN_LEFT || mouseButton == MOUSE_BTN_RIGHT)
                && stackCarriedByMouse.isEmpty() &&
                this.canClick()) {
            if (selected != stackUnderMouse.getItem()){
                selected = stackUnderMouse.getItem();
            }
            else {
                PacketHandler.sendToServer(new ShopRequestPKT(mouseButton, this.stackUnderMouse.copy(), Screen.hasShiftDown(), Screen.hasControlDown()));
            }

            this.lastClick = System.currentTimeMillis();
        }
        else if (stackUnderMouse.isEmpty()){
            selected = Items.AIR;
        }
        else if (!stackCarriedByMouse.isEmpty() && inField((int) mouseX, (int) mouseY) &&
                this.canClick()) {
            //0 isd getDim()
            //PacketRegistry.INSTANCE.sendToServer(new InsertMessage(0, mouseButton));
            this.lastClick = System.currentTimeMillis();
        }
    }

    private boolean inField(int mouseX, int mouseY) {
        return mouseX > (gui.getGuiLeft() + 7) && mouseX < (gui.getGuiLeft() + 176 - 7)
                && mouseY > (gui.getGuiTop() + 7) && mouseY < (gui.getGuiTop() + fieldHeight);
    }

    public void sortStackWrappers(List<ItemStack> stacksToDisplay) {
        Collections.sort(stacksToDisplay, new Comparator<ItemStack>() {

            final int mul = gui.getDownwards() ? -1 : 1;

            @Override
            public int compare(ItemStack o2, ItemStack o1) {
                return switch (gui.getSort()) {
                    case AMOUNT -> Integer.compare(o1.getCount(), o2.getCount()) * mul;
                    case NAME -> o2.getHoverName().getString().compareToIgnoreCase(o1.getHoverName().getString()) * mul;
                    case MOD -> ItemHelper.getModNameForItem(o2.getItem()).compareToIgnoreCase(ItemHelper.getModNameForItem(o1.getItem())) * mul;
                };
            }
        });
    }

    public void render() {
        switch (gui.getSort()) {
            case AMOUNT:
                sortBtn.setTextureId(TextureEnum.SORT_AMT);
                break;
            case MOD:
                sortBtn.setTextureId(TextureEnum.SORT_MOD);
                break;
            case NAME:
                sortBtn.setTextureId(TextureEnum.SORT_NAME);
                break;
        }
        focusBtn.setTextureId(gui.getAutoFocus() ? TextureEnum.RED : TextureEnum.GREY);
        directionBtn.setTextureId(gui.getDownwards() ? TextureEnum.SORT_DOWN : TextureEnum.SORT_UP);
    }
}
