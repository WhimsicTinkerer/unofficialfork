package com.wdiscute.starcatcher.items.modifieritem;

import com.mojang.datafixers.util.Pair;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.AbstractMinigameModifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MinigameModifierItem extends Item
{
    @SafeVarargs
    public MinigameModifierItem(Item.Properties props, Pair<Identifier, Supplier<AbstractMinigameModifier>>... modifiers)
    {
        this(props, 1, modifiers);
    }

    @SafeVarargs
    public MinigameModifierItem(Item.Properties props, int maxStackSize, Pair<Identifier, Supplier<AbstractMinigameModifier>>... modifiers)
    {
        super(props
                .component(ModDataComponents.MINIGAME_MODIFIERS, getAsList(modifiers))
                .stacksTo(maxStackSize)
        );
    }

    @SafeVarargs
    static List<Identifier> getAsList(Pair<Identifier, Supplier<AbstractMinigameModifier>>... modifiers)
    {
        List<Identifier> list = new ArrayList<>();
        for (Pair<Identifier, Supplier<AbstractMinigameModifier>> p : modifiers)
        {
            list.add(p.getFirst());
        }
        return list;
    }

}
