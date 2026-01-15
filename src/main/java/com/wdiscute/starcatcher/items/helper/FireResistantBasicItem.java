package com.wdiscute.starcatcher.items.helper;

import net.minecraft.world.item.Item;

public class FireResistantBasicItem extends Item
{
    public FireResistantBasicItem(Item.Properties props)
    {
        super(props.fireResistant());
    }
}
