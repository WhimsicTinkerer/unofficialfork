package com.wdiscute.starcatcher.items.modifieritem;

import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class TackleSkinItem extends Item
{
    public TackleSkinItem(Identifier tackleSkin)
    {
        this(1, tackleSkin);
    }

    public TackleSkinItem(int maxStackSize, Identifier rl)
    {
        super(new Item.Properties()
                .component(ModDataComponents.TACKLE_SKIN, rl)
                .stacksTo(maxStackSize)
        );
    }
}
