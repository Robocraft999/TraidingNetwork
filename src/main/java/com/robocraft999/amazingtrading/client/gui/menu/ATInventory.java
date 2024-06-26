package com.robocraft999.amazingtrading.client.gui.menu;

import com.robocraft999.amazingtrading.api.capabilities.IResourcePointProvider;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import com.robocraft999.amazingtrading.utils.PlayerHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.math.BigInteger;
import java.util.List;

public class ATInventory extends CombinedInvWrapper {
    public final Player player;
    public final IResourcePointProvider provider;

    public ATInventory(Player player, IItemHandlerModifiable... handlers){
        super(handlers);
        this.player = player;
        this.provider = player.getCapability(ATCapabilities.RESOURCE_POINT_CAPABILITY).orElseThrow(NullPointerException::new);
    }

    public boolean isServer() {
        return !player.level().isClientSide;
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
        //Note: We act as if there is no "max" RP for the player given we use a BigInteger
        // This means we don't have to try to put the overflow into the lock slot if there is an RP storage item there
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
        //Note: We act as if there is no "max" RP for the player given we use a BigInteger
        // This means we don't need to first try removing it from the lock slot as it will auto drain from the lock slot
        if (value.compareTo(currentEmc) > 0) {
            //Remove from provider first
            //This code runs first to simplify the logic
            //But it simulates removal first by extracting the amount from value and then removing that excess from items
            value = currentEmc;
        }
        updateResourcePointsAndSync(currentEmc.subtract(value));
    }

    /**
     * @apiNote Call on server only
     */
    public void syncChangedSlots(List<Integer> slotsChanged, IResourcePointProvider.TargetUpdateType updateTargets) {
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
        PlayerHelper.updateScore((ServerPlayer) player, PlayerHelper.SCOREBOARD_RP, points);
    }
}
