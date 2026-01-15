package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.mojang.datafixers.util.Pair;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public interface ModTackleSkins
{
    DeferredRegister<Supplier<ITackleSkin>> REGISTRY =
            DeferredRegister.create(Starcatcher.TACKLE_SKIN_REGISTRY, Starcatcher.MOD_ID);

    //base
    Identifier BASE_TACKLE_SKIN = registerCatchModifier("base", BaseTackleSkin::new);

    //pearl
    Identifier PEARL_TACKLE_SKIN = registerCatchModifier("pearl", PearlTackleSkin::new);

    //kimbe
    Identifier KIMBE_TACKLE_SKIN = registerCatchModifier("kimbe", KimbeTackleSkin::new);

    //frog
    Identifier FROG_TACKLE_SKIN = registerCatchModifier("frog", FrogTackleSkin::new);

    //colorful
    Identifier COLORFUL_TACKLE_SKIN = registerCatchModifier("colorful", ColorfulTackleSkin::new);

    //clear
    Identifier CLEAR_TACKLE_SKIN = registerCatchModifier("clear", ClearTackleSkin::new);

    //king
    Identifier KING_TACKLE_SKIN = registerCatchModifier("king", KingTackleSkin::new);

    static Identifier registerCatchModifier(String name, Supplier<ITackleSkin> sup)
    {
        REGISTRY.register(name, () -> sup);
        return Starcatcher.rl(name);
    }

    static void register(IEventBus eventBus)
    {
        REGISTRY.register(eventBus);
    }

    /**
     * Gets the tackle skin for sound callbacks.
     * Tackle skin classes are now server-safe (no client-only imports).
     */
    static ITackleSkin get(Level level, ItemStack itemInHand)
    {
        if (ModDataComponents.has(itemInHand, ModDataComponents.TACKLE_SKIN))
        {
            Identifier rl = ModDataComponents.get(itemInHand, ModDataComponents.TACKLE_SKIN);

            Optional<Supplier<ITackleSkin>> optional = level.registryAccess().lookupOrThrow(Starcatcher.TACKLE_SKIN).getOptional(rl);
            if (optional.isPresent()) return optional.get().get();
        }
        return new DefaultTackleSkin();
    }
}
