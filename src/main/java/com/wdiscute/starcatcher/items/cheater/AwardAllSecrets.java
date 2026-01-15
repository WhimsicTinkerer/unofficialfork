package com.wdiscute.starcatcher.items.cheater;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardAllSecrets extends Item
{
    public AwardAllSecrets(Item.Properties props)
    {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        //awards all secrets
        Map<Identifier, Integer> trophies = FishingGuideAttachment.getTrophiesCaught(player);

        level.registryAccess().lookupOrThrow(Starcatcher.TROPHY_REGISTRY).forEach(
                tp ->
                {
                    if(tp.trophyType() == TrophyProperties.TrophyType.SECRET)
                        trophies.putIfAbsent(U.getRlFromTp(level, tp), 99);
                });

        FishingGuideAttachment.setTrophiesCaught(player, trophies);

        return InteractionResult.SUCCESS;
    }

}
