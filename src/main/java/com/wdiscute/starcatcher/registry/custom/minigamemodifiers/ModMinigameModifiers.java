package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.mojang.datafixers.util.Pair;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ModMinigameModifiers
{
    DeferredRegister<Supplier<AbstractMinigameModifier>> REGISTRY =
            DeferredRegister.create(Starcatcher.MINIGAME_MODIFIERS_REGISTRY, Starcatcher.MOD_ID);

    //ice fishes
    Pair<Identifier, Supplier<AbstractMinigameModifier>> FREEZE_ON_MISS = registerMinigameModifier("freeze_on_miss", FreezeOnMissModifier::new);

    //aurora?
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SPAWN_FROZEN_SWEET_SPOTS = registerMinigameModifier("spawn_frozen_sweet_spots", SpawnFrozenSweetSpotsModifier::new);

    //base modifier
    Pair<Identifier, Supplier<AbstractMinigameModifier>> LOW_CHANCE_TREASURE_SPAWN = registerMinigameModifier("low_chance_treasure_spawn", LowChanceTreasureSpawnModifier::new);

    //gunpowder bait
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SPAWN_TNT_SWEET_SPOTS = registerMinigameModifier("spawn_tnt_sweet_spots", SpawnTntSweetSpotsModifier::new);

    //faster spawn speed
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SPAWN_TNT_SWEET_SPOTS_PLUS = registerMinigameModifier("spawn_tnt_sweet_spots_plus", () -> new SpawnTntSweetSpotsModifier(0.50f, 4));

    //shiny hook
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SPAWN_TREASURE_ON_THREE_HITS = registerMinigameModifier("spawn_treasure_on_three_hits", ShinyHookModifier::new);

    //heavy hook
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SLOWER_MOVING_SWEET_SPOTS = registerMinigameModifier("slower_moving_sweet_spots", () -> new HeavyHookModifier(2));
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SLOWER_MOVING_SWEET_SPOTS_MORE = registerMinigameModifier("slower_moving_sweet_spots_more", () -> new HeavyHookModifier(3));
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SLOWER_MOVING_SWEET_SPOTS_MORE_MORE = registerMinigameModifier("slower_moving_sweet_spots_more_more", () -> new HeavyHookModifier(4));
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SLOWER_MOVING_SWEET_SPOTS_MORE_MORE_MORE = registerMinigameModifier("slower_moving_sweet_spots_more_more_more", () -> new HeavyHookModifier(5));

    //stone hook
    Pair<Identifier, Supplier<AbstractMinigameModifier>> STOP_DECAY_ON_HIT = registerMinigameModifier("stop_decay_on_hit", StoneHookModifier::new);

    //mossy hook
    Pair<Identifier, Supplier<AbstractMinigameModifier>> HARDER_WITH_TREASURE_ON_PERFECT = registerMinigameModifier("harder_with_treasure_on_perfect", MossyHookModifier::new);

    //steady bobber
    Pair<Identifier, Supplier<AbstractMinigameModifier>> BIGGER_GREEN_SWEET_SPOTS = registerMinigameModifier("bigger_green_sweet_spots", SteadyBobberModifier::new);

    //clear bobber
    Pair<Identifier, Supplier<AbstractMinigameModifier>> SLOWER_VANISHING = registerMinigameModifier("slower_vanishing", ClearBobberModifier::new);

    //aqua bobber
    Pair<Identifier, Supplier<AbstractMinigameModifier>> ADD_AQUA_SWEET_SPOT = registerMinigameModifier("add_aqua_sweet_spot", () -> new AquaBobberModifier(1));

    //unused
    Pair<Identifier, Supplier<AbstractMinigameModifier>> ADD_THREE_AQUA_SWEET_SPOT = registerMinigameModifier("add_three_aqua_sweet_spot", () -> new AquaBobberModifier(3));

    //MultiLayer
    Pair<Identifier, Supplier<AbstractMinigameModifier>> NIKDO53_MODIFIER = registerMinigameModifier("nikdo53_modifier", Nikdo53Modifier::new);


    static Pair<Identifier, Supplier<AbstractMinigameModifier>> registerMinigameModifier(String name, Supplier<AbstractMinigameModifier> sup)
    {
        REGISTRY.register(name, () -> sup);
        return Pair.of(Starcatcher.rl(name), sup);
    }

    static void register(IEventBus eventBus)
    {
        REGISTRY.register(eventBus);
    }
}
