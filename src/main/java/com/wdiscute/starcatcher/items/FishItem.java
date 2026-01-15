package com.wdiscute.starcatcher.items;

import com.wdiscute.starcatcher.fishentity.FishEntity;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class FishItem extends Item
{
    public FishItem(Item.Properties props)
    {
        super(props.food(ModFoodProperties.BASIC_RAW_FISH));
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if(context.getPlayer().isCreative())
        {
            FishEntity fe = new FishEntity(ModEntities.FISH.get(), context.getLevel());
            fe.setFish(context.getItemInHand().copyWithCount(1));
            fe.setPos(context.getClickedPos().relative(context.getClickedFace()).getCenter());

            context.getLevel().addFreshEntity(fe);

            ItemStack is = context.getPlayer().getItemInHand(context.getHand());
            is.shrink(1);

            context.getPlayer().setItemInHand(context.getHand(), is);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
