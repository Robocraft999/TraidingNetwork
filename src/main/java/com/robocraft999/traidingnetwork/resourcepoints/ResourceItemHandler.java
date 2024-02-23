package com.robocraft999.traidingnetwork.resourcepoints;

import com.robocraft999.traidingnetwork.TraidingNetwork;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;

public class ResourceItemHandler extends SavedData {

    RItemStackHandler items = new RItemStackHandler();

    @Nonnull
    public static ResourceItemHandler get(Level level){
        if (level.isClientSide){
            throw new RuntimeException("Accessing ResourceItemHandler on client-side is not allowed!");
        }

        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(ResourceItemHandler::new, ResourceItemHandler::new, "resourceitemhandler");
    }

    public ResourceItemHandler(){
    }

    public ResourceItemHandler(CompoundTag tag){
        items.deserializeNBT(tag.getCompound("rpitems"));
        //print();
    }

    public boolean put(ItemStack stack){
        for (int i = 0; i < items.getSlots(); i++){
            if (items.getStackInSlot(i).getItem() == stack.getItem() || items.getStackInSlot(i) == ItemStack.EMPTY){
                ItemStack remainder = items.insertItem(i, stack, false);
                setDirty();
                return remainder == ItemStack.EMPTY;
            }
        }
        ItemStack remainder = items.insertItem(items.getSlots(), stack, false);
        setDirty();
        return remainder == ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getStacks(){
        return items.getItems();
    }

    public RItemStackHandler getHandler(){
        return this.items;
    }

    public void print(){
        for (int i = 0; i < items.getSlots(); i++){
            TraidingNetwork.LOGGER.debug("d " + items.getStackInSlot(i));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("rpitems", items.serializeNBT());
        return tag;
    }
}
