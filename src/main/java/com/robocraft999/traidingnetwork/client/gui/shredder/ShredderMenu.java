package com.robocraft999.traidingnetwork.client.gui.shredder;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.client.gui.menu.TNContainerMenu;
import com.robocraft999.traidingnetwork.client.gui.shredder.slots.SlotConsume;
import com.robocraft999.traidingnetwork.client.gui.shredder.slots.SlotInput;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.registry.TNMenuTypes;
import com.robocraft999.traidingnetwork.resourcepoints.RItemStackHandler;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShredderMenu extends TNContainerMenu {
    public final ShredderInventory shredderInventory;
    protected final List<Slot> inputSlots = new ArrayList<>();
    public final BlockPos blockPos;

    public ShredderMenu(Inventory playerInv, int i, BlockPos blockPos) {
        super(TNMenuTypes.SHREDDER_MENU.get(), playerInv, i);
        this.shredderInventory = new ShredderInventory(playerInv.player);
        this.blockPos = blockPos;
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
                player.level().getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(provider -> {
                    /*if (provider.getSlotsHandler() instanceof RItemStackHandler handler){
                        handler.put(stackToInsert);
                    }
                    provider.syncSlots((ServerPlayer) player, new ArrayList<>(), IResourceItemProvider.TargetUpdateType.ALL);*/
                    if (provider.getSlotsHandler() instanceof RItemStackHandler handler && !handler.hasFreeSlot(stackToInsert)){
                        handler.enlarge();
                    }
                    ItemHandlerHelper.insertItemStacked(provider.getSlotsHandler(), stackToInsert.copy(), false);
                    provider.sync();
                });
            }
            currentSlot.set(ItemStack.EMPTY);
        }

        return ItemStack.EMPTY;
    }
}
