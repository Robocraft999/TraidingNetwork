package com.robocraft999.traidingnetwork.gui.slots;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourceItemProvider;
import com.robocraft999.traidingnetwork.gui.menu.ShopInventory;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ShopSlot extends SlotItemHandler {
    private final ShopInventory inv;

    public ShopSlot(ShopInventory inv, int index, int x, int y){
        super(inv, index, x, y);
        this.inv = inv;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        ItemStack stack = super.remove(amount);
        if (!stack.isEmpty() && inv.isServer()){
            inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), IResourceItemProvider.TargetUpdateType.IF_NEEDED);
            inv.removeResourcePoints(BigInteger.valueOf(ResourcePointHelper.getResourcePointValue(stack)).multiply(BigInteger.valueOf(amount)));
        }
        return stack;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        AtomicLong newDecrement = new AtomicLong();
        player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
            newDecrement.set(Math.min(getItem().getMaxStackSize(), Math.min(decrement, cap.getPoints().longValue() / ResourcePointHelper.getResourcePointValue(getItem()))));
        });
        TraidingNetwork.LOGGER.debug(count + " " + decrement + " " + newDecrement.intValue());
        return super.tryRemove(count, newDecrement.intValue(), player);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        if (inv.isServer()) {
            if (stack.isEmpty()) {
                inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), IResourceItemProvider.TargetUpdateType.ALL);
            } else {
                inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), IResourceItemProvider.TargetUpdateType.NONE);
            }
        }
    }
}
