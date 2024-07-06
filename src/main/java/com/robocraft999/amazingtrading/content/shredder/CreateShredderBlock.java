package com.robocraft999.amazingtrading.content.shredder;

import com.robocraft999.amazingtrading.api.kinetics.blockentity.IOwnedBlockEntity;
import com.robocraft999.amazingtrading.registry.ATBlockEntities;
import com.robocraft999.amazingtrading.registry.ATShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class CreateShredderBlock extends AbstractShredderBlock<CreateShredderBlockEntity> {

    public CreateShredderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CreateShredderBlockEntity> getBlockEntityClass() {
        return CreateShredderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateShredderBlockEntity> getBlockEntityType() {
        return ATBlockEntities.CREATE_SHREDDER.get();
    }
}
