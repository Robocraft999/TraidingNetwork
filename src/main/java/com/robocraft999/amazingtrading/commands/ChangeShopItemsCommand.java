package com.robocraft999.amazingtrading.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.robocraft999.amazingtrading.AmazingTrading;
import com.robocraft999.amazingtrading.registry.ATCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ChangeShopItemsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("amtr")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("change")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(ChangeShopItemsCommand::execute)));

        dispatcher.register(builder);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        AmazingTrading.LOGGER.debug("Executing amt increment command with amount: {}", amount);

        try {
            player.getCapability(ATCapabilities.RESOURCE_ITEM_CAPABILITY).ifPresent(provider -> {
                IItemHandlerModifiable itemHandler = (IItemHandlerModifiable) provider.getSlotsHandler();

                for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        stack.grow(amount);
                        itemHandler.setStackInSlot(slot, stack);
                    }
                }

                provider.sync();
            });

            context.getSource().sendSuccess(() -> Component.literal("Changed item amounts in the shop by " + amount), true);
        } catch (Exception e) {
            AmazingTrading.LOGGER.error("Error executing amt change command", e);
            context.getSource().sendFailure(Component.literal("An error occurred while executing the command: " + e.getMessage()));
        }

        return 1;
    }
}
