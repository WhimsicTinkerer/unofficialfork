package com.wdiscute.starcatcher.items.cheater;

import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RevokeAllSecrets extends Item
{
    public RevokeAllSecrets()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {

        FishingGuideAttachment.getTrophiesCaught(player).keySet().removeIf(loc -> U.getTpFromRl(level, loc).trophyType() == TrophyProperties.TrophyType.SECRET);
        FishingGuideAttachment.sync(player);

        return InteractionResult.SUCCESS;
    }

}
