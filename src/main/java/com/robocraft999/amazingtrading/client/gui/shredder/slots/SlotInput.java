package com.robocraft999.amazingtrading.client.gui.shredder.slots;

import com.robocraft999.amazingtrading.api.capabilities.IResourcePointProvider.TargetUpdateType;
import com.robocraft999.amazingtrading.client.gui.shredder.ShredderInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SlotInput extends SlotItemHandler {
    private final ShredderInventory inv;

    public SlotInput(ShredderInventory inv, int index, int x, int y){
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        ItemStack stack = super.remove(amount);
        if (!stack.isEmpty() && inv.isServer()){
            inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.IF_NEEDED);
        }
        return stack;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        if (inv.isServer()) {
            if (stack.isEmpty()) {
                inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
            } else {
                /*if (EMCHelper.doesItemHaveEmc(stack)) {
                    inv.handleKnowledge(stack);
                }*/
                /*Optional<IItemEmcHolder> capability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
                if (capability.isPresent()) {
                    IItemEmcHolder emcHolder = capability.get();
                    //Get the points that the inventory has that is not in any stars
                    long shrunkenAvailableEMC = MathUtils.clampToLong(inv.provider.getEmc());
                    //try to insert it
                    long actualInserted = emcHolder.insertEmc(stack, shrunkenAvailableEMC, EmcAction.EXECUTE);
                    if (actualInserted > 0) {
                        //if we actually managed to insert some sync the slots changed, but don't update targets
                        // as that will be done by removing the points and syncing how much is stored there
                        inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
                        inv.removeEmc(BigInteger.valueOf(actualInserted));
                    } else if (emcHolder.getStoredEmc(stack) > 0) {
                        //If we didn't manage to insert any into our star, and we do have points stored
                        // update the targets
                        inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
                    } else {
                        //If we didn't manage to insert any into our star, and we don't have any points stored
                        // don't bother updating the targets
                        inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
                    }
                } else {*/
                    //Update the fact the slots changed but don't bother updating targets
                    inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
                //}
            }
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
