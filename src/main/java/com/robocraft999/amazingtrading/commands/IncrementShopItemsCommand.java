package com.robocraft999.amazingtrading.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.api.capabilities.IResourceItemProvider;
import com.robocraft999.amazingtrading.client.gui.shop.ShopInventory;
import com.robocraft999.amazingtrading.net.PacketHandler;
import com.robocraft999.amazingtrading.net.packets.shop.IncrementShopItemsPKT;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;

public class IncrementShopItemsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("amt")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("increment")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(IncrementShopItemsCommand::execute)));

        dispatcher.register(builder);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        AmazingTrading.LOGGER.debug("Executing amt increment command with amount: {}", amount);

        ShopInventory shopInventory = new ShopInventory(player);
        IItemHandlerModifiable itemHandler = (IItemHandlerModifiable) shopInventory.itemProvider.getSlotsHandler();

        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                stack.grow(amount);
                itemHandler.setStackInSlot(slot, stack);
            }
        }

        shopInventory.itemProvider.syncSlots((ServerPlayer) player, null, IResourceItemProvider.TargetUpdateType.ALL);

        context.getSource().sendSuccess(() -> Component.literal("Incremented all items in the shop by " + amount), true);

        return 1;
    }
}
