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
    public MinigameModifierItem(Pair<Identifier, Supplier<AbstractMinigameModifier>>... modifiers)
    {
        this(1, modifiers);
    }

    @SafeVarargs
    public MinigameModifierItem(int maxStackSize, Pair<Identifier, Supplier<AbstractMinigameModifier>>... modifiers)
    {
        super(new Item.Properties()
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
