package com.wdiscute.starcatcher.items.modifieritem;

import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class TackleSkinItem extends Item
{
    public TackleSkinItem(Item.Properties props, Identifier tackleSkin)
    {
        this(props, 1, tackleSkin);
    }

    public TackleSkinItem(Item.Properties props, int maxStackSize, Identifier rl)
    {
        super(props
                .component(ModDataComponents.TACKLE_SKIN, rl)
                .stacksTo(maxStackSize)
        );
    }
}
