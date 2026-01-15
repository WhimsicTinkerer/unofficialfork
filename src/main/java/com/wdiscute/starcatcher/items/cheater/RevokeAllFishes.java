package com.wdiscute.starcatcher.items.cheater;

import com.wdiscute.starcatcher.io.*;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class RevokeAllFishes extends Item
{
    public RevokeAllFishes()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        //reset fishes caught
        FishingGuideAttachment.setFishesCaught(player, new HashMap<>());

        return InteractionResult.SUCCESS;
    }

}
