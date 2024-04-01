package com.robocraft999.amazingtrading.resourcepoints;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class RItemStackHandler extends ItemStackHandler {

    public RItemStackHandler() {
        super(0);
    }

    public void changeSize(int size) {
        NonNullList<ItemStack> newStacks = NonNullList.withSize(((size / 9) + 1) * 9, ItemStack.EMPTY);
        for (int i = 0; i < getSlots(); i++) {
            newStacks.set(i, getStackInSlot(i));
        }
        stacks = newStacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return getSlotLimit(slot);
    }

    public NonNullList<ItemStack> getItems() {
        //AmazingTrading.LOGGER.info("des get: "+stacks);
        return this.stacks;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == getSlots()) {
            changeSize(slot);
        } else if (slot > getSlots()) {
            throw new UnsupportedOperationException("Slot index out of bounds");
        }
        return super.insertItem(slot, stack, simulate);
    }

    public boolean put(ItemStack stack){
        if (stack == ItemStack.EMPTY)return false;
        for (int i = 0; i < getSlots(); i++){
            if (getStackInSlot(i).getItem() == stack.getItem() || getStackInSlot(i) == ItemStack.EMPTY){
                ItemStack remainder = insertItem(i, stack, false);
                return remainder == ItemStack.EMPTY;
            }
        }
        ItemStack remainder = insertItem(getSlots(), stack, false);
        return remainder == ItemStack.EMPTY;
    }

    public boolean hasFreeSlot(ItemStack stackInSlot) {
        return stacks.stream().anyMatch((stack) -> stack.isEmpty() || stack.is(stackInSlot.getItem()));
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < this.stacks.size(); ++i) {
            if (!((ItemStack)this.stacks.get(i)).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                itemTag.putInt("RealCount", this.stacks.get(i).getCount());
                ((ItemStack)this.stacks.get(i)).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        ListTag tagList = nbt.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                ItemStack desItemStack = ItemStack.of(itemTags);
                desItemStack.setCount(itemTags.getInt("RealCount"));
                //AmazingTrading.LOGGER.info("stack: "+desItemStack);
                this.stacks.set(slot, desItemStack);
            }
        }
        //AmazingTrading.LOGGER.info("des stacks: "+stacks);

        this.onLoad();
    }

    public void enlarge(){
        changeSize(getSlots() + 1);
    }
}
