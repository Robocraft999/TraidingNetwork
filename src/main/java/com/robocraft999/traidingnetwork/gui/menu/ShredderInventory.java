package com.robocraft999.traidingnetwork.gui.menu;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider.TargetUpdateType;
import com.robocraft999.traidingnetwork.api.capabilities.IResourcePointProvider;
import com.robocraft999.traidingnetwork.api.resourcepoints.ItemNetwork;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
inspired by https://github.com/sinkillerj/ProjectE/blob/mc1.20.x/src/main/java/moze_intel/projecte/gameObjs/container/inventory/TransmutationInventory.java
 */
public class ShredderInventory extends CombinedInvWrapper {

    public final Player player;
    public final IResourcePointProvider provider;
    private final IItemHandlerModifiable inputLocks;

    public ShredderInventory(Player player){
        super((IItemHandlerModifiable) player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).orElseThrow(NullPointerException::new).getInputAndLocks());
        this.inputLocks = itemHandler[0];
        this.player = player;
        this.provider = player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).orElseThrow(NullPointerException::new);
        if (!isServer()) {
            //updateClientTargets();
        }
    }

    /**
     * @apiNote Call on client only
     */
    public void checkForUpdates() {
        //long matterEmc = ResourcePointHelper.getResourcePointValue(outputs.getStackInSlot(0));
        /*if (BigInteger.valueOf(Math.max(matterEmc, fuelEmc)).compareTo(getAvailableEmc()) > 0) {
            updateClientTargets();
        }*/
    }

    public void updateClientTargets() {
        if (isServer()) {
            return;
        }
    }

    public boolean isServer() {
        return !player.level().isClientSide;
    }

    public void addResourcePoints(BigInteger value){
        int compareToZero = value.compareTo(BigInteger.ZERO);
        if (compareToZero == 0) {
            //Optimization to not look at the items if nothing will happen anyways
            return;
        } else if (compareToZero < 0) {
            //Make sure it is using the correct method so that it handles the klein stars properly
            removeResourcePoints(value.negate());
            return;
        }
        List<Integer> inputLocksChanged = new ArrayList<>();
        syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
        //Note: We act as if there is no "max" EMC for the player given we use a BigInteger
        // This means we don't have to try to put the overflow into the lock slot if there is an EMC storage item there
        updateResourcePointsAndSync(provider.getPoints().add(value));
    }

    public void removeResourcePoints(BigInteger value){
        int compareToZero = value.compareTo(BigInteger.ZERO);
        if (compareToZero == 0) {
            //Optimization to not look at the items if nothing will happen anyways
            return;
        } else if (compareToZero < 0) {
            //Make sure it is using the correct method so that it handles the klein stars properly
            addResourcePoints(value.negate());
            return;
        }
        BigInteger currentEmc = provider.getPoints();
        //Note: We act as if there is no "max" EMC for the player given we use a BigInteger
        // This means we don't need to first try removing it from the lock slot as it will auto drain from the lock slot
        if (value.compareTo(currentEmc) > 0) {
            //Remove from provider first
            //This code runs first to simplify the logic
            //But it simulates removal first by extracting the amount from value and then removing that excess from items
            List<Integer> inputLocksChanged = new ArrayList<>();
            BigInteger toRemove = value.subtract(currentEmc);
            value = currentEmc;

            //Sync the changed slots if any have changed
            syncChangedSlots(inputLocksChanged, TargetUpdateType.NONE);
        }
        updateResourcePointsAndSync(currentEmc.subtract(value));
    }

    /**
     * @apiNote Call on server only
     */
    public void syncChangedSlots(List<Integer> slotsChanged, IResourcePointProvider.TargetUpdateType updateTargets) {
        provider.syncInputAndLocks((ServerPlayer) player, slotsChanged, updateTargets);
    }

    /**
     * @apiNote Call on server only
     */
    private void updateResourcePointsAndSync(BigInteger points) {
        if (points.compareTo(BigInteger.ZERO) < 0) {
            //Clamp the points, should never be less than zero but just in case make sure to fix it
            points = BigInteger.ZERO;
        }
        provider.setPoints(points);
        provider.syncPoints((ServerPlayer) player);
        //PlayerHelper.updateScore((ServerPlayer) player, PlayerHelper.SCOREBOARD_POINTS, points);
    }

    public void addItemToNetwork(ItemStack stack) {
        TraidingNetwork.LOGGER.info("TT" + stack);
        for (Slot slot : ItemNetwork.INSTANCE.slots){
            if (slot.getItem().equals(stack)){
                slot.getItem().grow(stack.getCount());
                return;
            }
        }

        ItemNetwork.INSTANCE.slots.add(new Slot(this.player.getInventory(), ItemNetwork.INSTANCE.slots.size() + 36, 0, 0));
    }
}
