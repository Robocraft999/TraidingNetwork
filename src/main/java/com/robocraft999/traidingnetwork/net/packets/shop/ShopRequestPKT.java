package com.robocraft999.traidingnetwork.net.packets.shop;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.net.ITNPacket;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.net.SyncItemProviderPKT;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.resourcepoints.RItemStackHandler;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.robocraft999.traidingnetwork.gui.menu.ShopWidget.MOUSE_BTN_RIGHT;

/** Packet that is sent when an Item is requested through ui e.g. a button or click on slot
 *
 */

public class ShopRequestPKT implements ITNPacket {
    private int mouseButton = 0;
    private ItemStack stack = ItemStack.EMPTY;
    private boolean shift;
    private boolean ctrl;

    public ShopRequestPKT(int mouseButton, ItemStack stack, boolean shift, boolean ctrl) {
        this.mouseButton = mouseButton;
        this.stack = stack.copy();
        if (mouseButton == MOUSE_BTN_RIGHT){
            this.stack.setCount(1);
        } else if (this.stack.getCount() > stack.getMaxStackSize()) {
            this.stack.setCount(stack.getMaxStackSize()); //important or it will be killed by a filter
        }
        this.shift = shift;
        this.ctrl = ctrl;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        ServerLevel level = player.serverLevel();
        level.getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent( cap -> {
            RItemStackHandler handler = (RItemStackHandler) cap.getSlotsHandler();
            for (int i = 0; i < handler.getSlots(); i++){
                ItemStack serverStack = handler.getStackInSlot(i);
                if (serverStack.isEmpty())continue;

                if (serverStack.is(this.stack.getItem())){
                    AtomicLong newDecrement = new AtomicLong();
                    player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap2 -> {
                        newDecrement.set(Math.min(this.stack.getCount(), cap2.getPoints().longValue() / ResourcePointHelper.getResourcePointValue(this.stack)));
                    });

                    ItemStack extracted = handler.extractItem(i, newDecrement.intValue(), false);
                    player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap2 -> {
                        if (this.shift){
                            ItemHandlerHelper.giveItemToPlayer(player, extracted);
                        } else {
                            player.containerMenu.setCarried(extracted);
                            player.containerMenu.broadcastChanges();
                        }
                        cap2.setPoints(cap2.getPoints().subtract(BigInteger.valueOf(ResourcePointHelper.getResourcePointValue(this.stack) * newDecrement.get())));
                        cap2.syncPoints(player);
                        cap.sync(player);
                    });

                    break;
                }
            }
        });

    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.mouseButton);
        buf.writeItem(this.stack);
        buf.writeBoolean(this.shift);
        buf.writeBoolean(this.ctrl);
    }

    public static ShopRequestPKT decode(FriendlyByteBuf buf) {
        return new ShopRequestPKT(buf.readInt(), buf.readItem(), buf.readBoolean(), buf.readBoolean());
    }


}
