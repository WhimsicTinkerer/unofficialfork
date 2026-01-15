package com.wdiscute.starcatcher.items.modifieritem;

import com.mojang.datafixers.util.Pair;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.custom.catchmodifiers.AbstractCatchModifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CatchModifierItem extends Item
{
    @SafeVarargs
    public CatchModifierItem(Item.Properties props, Pair<Identifier, Supplier<AbstractCatchModifier>>... modifiers)
    {
        this(props, 1, modifiers);
    }

    @SafeVarargs
    public CatchModifierItem(Item.Properties props, int maxStackSize, Pair<Identifier, Supplier<AbstractCatchModifier>>... modifiers)
    {
        super(props
                .component(ModDataComponents.CATCH_MODIFIERS, getAsList(modifiers))
                .stacksTo(maxStackSize)
        );
    }

    @SafeVarargs
    static List<Identifier> getAsList(Pair<Identifier, Supplier<AbstractCatchModifier>>... modifiers)
    {
        List<Identifier> list = new ArrayList<>();
        for (Pair<Identifier, Supplier<AbstractCatchModifier>> p : modifiers)
        {
            list.add(p.getFirst());
        }
        return list;
    }

}
