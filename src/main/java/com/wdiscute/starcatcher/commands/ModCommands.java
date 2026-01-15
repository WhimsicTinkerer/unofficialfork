package com.wdiscute.starcatcher.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.io.network.FishingStartedPayload;
import com.wdiscute.starcatcher.registry.custom.catchmodifiers.AbstractCatchModifier;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.AbstractMinigameModifier;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ITackleSkin;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ModCommands
{
    private static final DynamicCommandExceptionType ERROR_ROD = new DynamicCommandExceptionType(
            o -> Component.translatableEscape("commands.starcatcher.rod_not_found", o)
    );

    private static final DynamicCommandExceptionType ERROR_EMPTY = new DynamicCommandExceptionType(
            o -> Component.translatableEscape("commands.starcatcher.rod_not_found", o)
    );

    private static final DynamicCommandExceptionType ERROR_FISH_ENTRY_INVALID = new DynamicCommandExceptionType(
            o -> Component.translatableEscape("commands.starcatcher.fish_entry_not_found", o)
    );

    private static final DynamicCommandExceptionType ERROR_MODIFIER_INVALID = new DynamicCommandExceptionType(
            o -> Component.translatableEscape("commands.starcatcher.modifier_not_found", o)
    );


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context)
    {
        dispatcher.register(Commands.literal("starcatcher")
                .requires(sourceStack -> {
                    try {
                        return sourceStack.getPlayer() != null && sourceStack.getServer().getPlayerList().isOp(sourceStack.getPlayer().nameAndId());
                    } catch (Exception e) {
                        return sourceStack.getServer() != null; // Allow for console/server
                    }
                })


                //starcatcher simulate_fish starcatcher:aurora
                .then(Commands.literal("simulate_fish")
                        .then(Commands.argument("fish_entry", ResourceArgument.resource(context, Starcatcher.FISH_REGISTRY))
                                .executes(c ->
                                        startMinigame(
                                                c.getSource().getPlayerOrException(),
                                                ResourceArgument.getResource(c, "fish_entry", Starcatcher.FISH_REGISTRY).unwrap().left().get()
                                        )
                                )
                        )
                )


                //starcatcher add_modifier starcatcher:freeze_on_miss
                .then(Commands.literal("add_minigame_modifier")
                        .then(Commands.argument("modifier", ResourceArgument.resource(context, Starcatcher.MINIGAME_MODIFIERS))
                                .executes(c ->
                                        addMinigameModifier(
                                                c.getSource().getPlayerOrException(),
                                                ResourceArgument.getResource(c, "modifier", Starcatcher.MINIGAME_MODIFIERS).unwrap().left().get()
                                        )
                                ))
                )

                //starcatcher add_modifier starcatcher:ignore_daytime_and_weather_restrictions
                .then(Commands.literal("add_catch_modifier")
                        .then(Commands.argument("modifier", ResourceArgument.resource(context, Starcatcher.CATCH_MODIFIERS))
                                .executes(c ->
                                        addCatchModifier(
                                                c.getSource().getPlayerOrException(),
                                                ResourceArgument.getResource(c, "modifier", Starcatcher.CATCH_MODIFIERS).unwrap().left().get()
                                        )
                                ))
                )

                //starcatcher add_tackle_skin starcatcher:ignore_daytime_and_weather_restrictions
                .then(Commands.literal("add_tackle_skin")
                        .then(Commands.argument("modifier", ResourceArgument.resource(context, Starcatcher.TACKLE_SKIN))
                                .executes(c ->
                                        addTackleSkin(
                                                c.getSource().getPlayerOrException(),
                                                ResourceArgument.getResource(c, "modifier", Starcatcher.TACKLE_SKIN).unwrap().left().get()
                                        )
                                ))
                )

                //starcatcher remove_catch_modifier
                .then(Commands.literal("remove_minigame_modifier")
                        .executes(c ->
                                removeMinigameModifier(
                                        c.getSource().getPlayerOrException()
                                )
                        )
                )

                //starcatcher remove_minigame_modifier
                .then(Commands.literal("remove_catch_modifier")
                        .executes(c ->
                                removeCatchModifier(
                                        c.getSource().getPlayerOrException()
                                )
                        )
                )


                .then(Commands.literal("award_fish")
                        .executes(c -> awardAllFish(c.getSource().getPlayerOrException()))
                        // -> /starcatcher award_fish all
                        .then(Commands.literal("all")
                                // -> /starcatcher award_fish all
                                .executes(c -> awardAllFish(c.getSource().getPlayerOrException()))
                                // -> /starcatcher award_fish all 0 0 0
                                .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                        .then(Commands.argument("size", IntegerArgumentType.integer())
                                                .then(Commands.argument("weight", IntegerArgumentType.integer())
                                                        .executes(c -> awardAllFish(
                                                                        c.getSource().getPlayerOrException(),
                                                                        IntegerArgumentType.getInteger(c, "ticks"),
                                                                        IntegerArgumentType.getInteger(c, "size"),
                                                                        IntegerArgumentType.getInteger(c, "weight")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        // -> starcatcher award_fish starcatcher:aurora
                        .then(Commands.argument("fish", ResourceArgument.resource(context, Starcatcher.FISH_REGISTRY))
                                .executes(c -> awardFish(
                                        c.getSource().getPlayerOrException(),
                                        ResourceArgument.getResource(c, "fish", Starcatcher.FISH_REGISTRY).unwrap().left().get(),
                                        0, 0, 0
                                ))
                                // -> /starcatcher award_fish 123, 123, 132
                                .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                        .then(Commands.argument("size", IntegerArgumentType.integer())
                                                .then(Commands.argument("weight", IntegerArgumentType.integer())
                                                        .executes(c -> awardFish(
                                                                        c.getSource().getPlayerOrException(),
                                                                        ResourceArgument.getResource(c, "fish", Starcatcher.FISH_REGISTRY).key(),
                                                                        IntegerArgumentType.getInteger(c, "ticks"),
                                                                        IntegerArgumentType.getInteger(c, "size"),
                                                                        IntegerArgumentType.getInteger(c, "weight")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                )


                .then(Commands.literal("revoke_fish")
                        // -> /starcatcher revoke_fish
                        .executes(c -> revokeAllFish(c.getSource().getPlayerOrException()))
                        // -> /starcatcher revoke_fish all
                        .then(Commands.literal("all").executes(c -> revokeAllFish(c.getSource().getPlayerOrException())))
                        // -> starcatcher revoke_fish starcatcher:aurora
                        .then(Commands.argument("fish", ResourceArgument.resource(context, Starcatcher.FISH_REGISTRY))
                                .executes(c -> revokeFish(
                                        c.getSource().getPlayerOrException(),
                                        ResourceArgument.getResource(c, "fish", Starcatcher.FISH_REGISTRY).key()
                                ))
                        )
                )

        );
    }

    private static int revokeAllFish(ServerPlayer player)
    {
        FishingGuideAttachment.getFishesCaught(player).clear();
        FishingGuideAttachment.sync(player);
        return 0;
    }

    private static int revokeFish(ServerPlayer player, ResourceKey<FishProperties> fish)
    {
        FishingGuideAttachment.getFishesCaught(player).remove(fish.identifier());
        FishingGuideAttachment.sync(player);
        return 0;
    }

    private static int awardAllFish(ServerPlayer player, int ticks, int size, int weight)
    {
        for (FishProperties fp : player.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
            FishCaughtCounter.awardFishCaughtCounter(fp, player, ticks, size, weight, false, false);

        return 0;
    }

    private static int awardAllFish(ServerPlayer player)
    {
        for (FishProperties fp : player.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
            FishCaughtCounter.awardFishCaughtCounter(fp, player, 0, 0, 0, false, false);

        return 0;
    }

    private static int awardFish(ServerPlayer player, ResourceKey<FishProperties> fish, int ticks, int size, int weight) throws CommandSyntaxException
    {
        Optional<FishProperties> optional = player.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY).getOptional(fish);
        if (optional.isPresent())
            FishCaughtCounter.awardFishCaughtCounter(optional.get(), player, ticks, size, weight, false, false);
        else
            throw ERROR_FISH_ENTRY_INVALID.create(fish);
        return 0;
    }

    private static int removeMinigameModifier(ServerPlayer player) throws CommandSyntaxException
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) throw ERROR_EMPTY.create(null);

        if (ModDataComponents.has(stack, ModDataComponents.MINIGAME_MODIFIERS))
        {
            ModDataComponents.remove(stack, ModDataComponents.MINIGAME_MODIFIERS);
        }
        return 1;
    }

    private static int removeCatchModifier(ServerPlayer player) throws CommandSyntaxException
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) throw ERROR_EMPTY.create(null);

        if (ModDataComponents.has(stack, ModDataComponents.CATCH_MODIFIERS))
        {
            ModDataComponents.remove(stack, ModDataComponents.CATCH_MODIFIERS);
        }
        return 1;
    }

    private static int addMinigameModifier(ServerPlayer player, ResourceKey<Supplier<AbstractMinigameModifier>> modifier) throws CommandSyntaxException
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) throw ERROR_EMPTY.create(null);

        if (ModDataComponents.has(stack, ModDataComponents.MINIGAME_MODIFIERS))
        {
            List<Identifier> mods = new ArrayList<>(ModDataComponents.get(stack, ModDataComponents.MINIGAME_MODIFIERS));
            mods.add(modifier.identifier());
            ModDataComponents.set(stack, ModDataComponents.MINIGAME_MODIFIERS, mods);
        }
        else
        {
            ModDataComponents.set(stack, ModDataComponents.MINIGAME_MODIFIERS, List.of(modifier.identifier()));
        }

        return 1;
    }

    private static int addCatchModifier(ServerPlayer player, ResourceKey<Supplier<AbstractCatchModifier>> modifier) throws CommandSyntaxException
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) throw ERROR_EMPTY.create(null);

        if (ModDataComponents.has(stack, ModDataComponents.CATCH_MODIFIERS))
        {
            List<Identifier> mods = new ArrayList<>(ModDataComponents.get(stack, ModDataComponents.CATCH_MODIFIERS));
            mods.add(modifier.identifier());
            ModDataComponents.set(stack, ModDataComponents.CATCH_MODIFIERS, mods);
        }
        else
        {
            ModDataComponents.set(stack, ModDataComponents.CATCH_MODIFIERS, List.of(modifier.identifier()));
        }

        return 1;
    }

    private static int addTackleSkin(ServerPlayer player, ResourceKey<Supplier<ITackleSkin>> tackleSkin) throws CommandSyntaxException
    {
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(StarcatcherTags.RODS)) throw ERROR_ROD.create(null);

        ModDataComponents.set(stack, ModDataComponents.TACKLE_SKIN, tackleSkin.identifier());

        return 1;
    }

    private static int startMinigame(ServerPlayer player, ResourceKey<FishProperties> fish) throws CommandSyntaxException
    {
        if (!player.getMainHandItem().is(StarcatcherTags.RODS)) throw ERROR_ROD.create(null);

        Optional<FishProperties> optional = player.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY).getOptional(fish);

        if (optional.isPresent())
        {
            PacketDistributor.sendToPlayer(player, new FishingStartedPayload(optional.get(), player.getMainHandItem()));
            return 1;
        }
        else
        {
            throw ERROR_FISH_ENTRY_INVALID.create(fish.identifier());
        }
    }
}
