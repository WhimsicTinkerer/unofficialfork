package com.wdiscute.starcatcher.items.helper;

import net.minecraft.world.item.Item;

public class SingleStackBasicItem extends Item
{
    public SingleStackBasicItem(Item.Properties props)
    {
        super(props.stacksTo(1));
    }
}
