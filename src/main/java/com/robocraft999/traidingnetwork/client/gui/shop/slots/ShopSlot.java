package com.robocraft999.traidingnetwork.client.gui.shop.slots;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourceItemProvider;
import com.robocraft999.traidingnetwork.client.gui.shop.ShopInventory;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;

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
            inv.removeResourcePoints(BigInteger.valueOf(ResourcePointHelper.getRPBuyCost(stack)).multiply(BigInteger.valueOf(amount)));
        }
        return stack;
    }

    @Override
    public @NotNull Optional<ItemStack> tryRemove(int count, int decrement, @NotNull Player player) {
        long newDecrement = Math.min(getItem().getMaxStackSize(), Math.min(decrement, inv.provider.getPoints().longValue() / ResourcePointHelper.getRPBuyCost(getItem())));
        if (newDecrement > Integer.MAX_VALUE){
            TraidingNetwork.LOGGER.warn("trying to extract more than Integer.MAX_VALUE items from ShopSlot");
        }
        TraidingNetwork.LOGGER.debug(count + " " + decrement + " " + newDecrement);
        return super.tryRemove(count, (int) newDecrement, player);
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
