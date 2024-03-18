package com.robocraft999.traidingnetwork.blockentity;

import com.mojang.authlib.GameProfile;
import com.robocraft999.traidingnetwork.Config;
import com.robocraft999.traidingnetwork.TraidingNetwork;
import com.robocraft999.traidingnetwork.block.CreateShredderBlock;
import com.robocraft999.traidingnetwork.gui.menu.ShredderMenu;
import com.robocraft999.traidingnetwork.net.PacketHandler;
import com.robocraft999.traidingnetwork.net.packets.shredder.SyncOwnerNamePKT;
import com.robocraft999.traidingnetwork.registry.TNCapabilities;
import com.robocraft999.traidingnetwork.resourcepoints.RItemStackHandler;
import com.robocraft999.traidingnetwork.utils.ResourcePointHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class CreateShredderBlockEntity extends KineticBlockEntity implements IOwnedBlockEntity, MenuProvider {
    protected UUID ownerId;
    protected String cachedOwnerName;
    private ItemStackHandler inputInv;
    public int timer;
    private LazyOptional<IItemHandler> capability;

    public CreateShredderBlockEntity(BlockEntityType<? extends CreateShredderBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandler(1);
        capability = LazyOptional.of(ShredderInventoryHandler::new);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0)
            return;

        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty())
            return;

        if (timer > 0) {
            timer -= getProcessingSpeed();

            if (level.isClientSide) {
                spawnParticles();
                return;
            }
            if (timer <= 0)
                process();
            return;
        }

        timer = Config.SHREDDER_PROCESS_TICKS.get();
    }

    private void process() {
        if(getLevel() == null) return;


        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (!canProcess(stackInSlot)) return;

        if (getLevel() != null && !getLevel().isClientSide && getOwnerId() != null) {
            ServerPlayer player = (ServerPlayer) getLevel().getPlayerByUUID(getOwnerId());
            if (player != null) {
                player.getCapability(TNCapabilities.RESOURCE_POINT_CAPABILITY).ifPresent(cap -> {
                    TraidingNetwork.LOGGER.debug("points: " + cap.getPoints() + " sellvalue: " + ResourcePointHelper.getRPSellValue(stackInSlot));
                    cap.setPoints(cap.getPoints().add(BigInteger.valueOf(ResourcePointHelper.getRPSellValue(stackInSlot))));
                    cap.syncPoints(player);
                });
            }
            var ow = getLevel().getServer().getPlayerList().getPlayer(getOwnerId());
            var ow2 = getLevel().getServer().getProfileCache().get(getOwnerId());
            TraidingNetwork.LOGGER.info("owner: '" + ow + "'" + " cache: '" + ow2 + "' name: " + (ow2.isPresent() ? ow2.get().getName(): "empty"));
            getLevel().getCapability(TNCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(cap2 -> {
                if (cap2.getSlotsHandler() instanceof RItemStackHandler handler && !handler.hasFreeSlot(stackInSlot)){
                    handler.enlarge();
                }
                ItemHandlerHelper.insertItemStacked(cap2.getSlotsHandler(), stackInSlot.copyWithCount(1), false);
                cap2.sync();
            });
            stackInSlot.shrink(1);
        }

        inputInv.setStackInSlot(0, stackInSlot);
        sendData();
        setChanged();
    }

    private Direction getEjectDirection() {
        var block = ((CreateShredderBlock) getBlockState().getBlock());
        var speed = getSpeed();
        block.getRotationAxis(getBlockState());
        boolean rotation = speed >= 0;
        Direction ejectDirection = Direction.UP;
        switch (block.getRotationAxis(getBlockState())) {
            case X -> {
                ejectDirection = rotation ? Direction.SOUTH : Direction.NORTH;
            }
            case Z -> {
                ejectDirection = rotation ? Direction.WEST : Direction.EAST;
            }
        }
        return ejectDirection;
    }

    public void spawnParticles() {
        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty())
            return;

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.5f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    @Override
    public void remove() {
        capability.invalidate();
        super.remove();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInv);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("InputInventory", inputInv.serializeNBT());
        compound.putUUID("ownerID", getOwnerId());
        compound.putString("ownerName", cachedOwnerName);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        timer = compound.getInt("Timer");
        inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        setOwner(compound.getUUID("ownerID"));
        cachedOwnerName = compound.getString("ownerName");
        super.read(compound, clientPacket);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap))
            return capability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public UUID getOwnerId() {
        return this.ownerId;
    }

    @Override
    public void setOwner(UUID ownerId) {
        this.ownerId = ownerId;
        if (getLevel() != null && !getLevel().isClientSide){
            this.cachedOwnerName = getLevel().getServer().getProfileCache().get(ownerId).orElse(new GameProfile(UUID.randomUUID(), "fake")).getName();
            PacketHandler.sendToNear(new SyncOwnerNamePKT(this.cachedOwnerName, getBlockPos()), getBlockPos(), getLevel());
        }

    }

    /*@Override
    public boolean canPlayerUse(Player player) {
        return player.getUUID().equals(getOwnerId());
    }*/

    public String getOwnerName(){
        return this.cachedOwnerName;
    }

    public void setOwnerName(String name){
        this.cachedOwnerName = name;
    }

    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    }

    public boolean canProcess(ItemStack stack) {
        return ResourcePointHelper.doesItemHaveRP(stack);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ShredderMenu(playerInventory, windowId, getBlockPos());
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.literal("test_name");//PELang.TRANSMUTATION_TRANSMUTE.translate();
    }

    private class ShredderInventoryHandler extends CombinedInvWrapper {

        public ShredderInventoryHandler() {
            super(inputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return canProcess(stack) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

    }
}
