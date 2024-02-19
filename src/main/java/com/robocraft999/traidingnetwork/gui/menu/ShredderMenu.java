package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.gui.slots.*;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShredderMenu extends TNContainerMenu {
    public final ShredderInventory shredderInventory;
    protected final List<Slot> inputSlots = new ArrayList<>();

    public ShredderMenu(Inventory playerInv, int i) {
        super(TNMenuTypes.SHREDDER_MENU.get(), playerInv, i);
        this.shredderInventory = new ShredderInventory(playerInv.player);
        initSlots();
    }

    protected void initSlots(){
        this.addSlot(new SlotInput(shredderInventory, 0, 43, 23));
        this.addSlot(new SlotConsume(shredderInventory, 9, 107, 97));
        addPlayerInventory(35, 117);
    }

    @Override
    protected @NotNull Slot addSlot(@NotNull Slot slot) {
        if (slot instanceof SlotInput input) {
            inputSlots.add(input);
        }
        return super.addSlot(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        Slot currentSlot = tryGetSlot(i);
        ItemStack slotStack = currentSlot.getItem();
        ItemStack stackToInsert = slotStack;
        long points = ResourcePointHelper.getRPSellValue(stackToInsert);
        if (points > 0){
            if (shredderInventory.isServer()) {
                BigInteger pointsBigInt = BigInteger.valueOf(points);
                shredderInventory.addResourcePoints(pointsBigInt.multiply(BigInteger.valueOf(stackToInsert.getCount())));
                TraidingNetwork.LOGGER.info("t"+shredderInventory.provider.getPoints());
                shredderInventory.addItemToNetwork(stackToInsert);
            }
            currentSlot.set(ItemStack.EMPTY);
        }

        return ItemStack.EMPTY;
    }
}
