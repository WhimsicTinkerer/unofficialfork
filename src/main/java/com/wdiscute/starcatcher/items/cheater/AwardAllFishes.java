package com.wdiscute.starcatcher.items.cheater;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class AwardAllFishes extends Item
{
    public AwardAllFishes()
    {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        if(!player.isCreative())
            return InteractionResult.PASS;

        //sets all fps on fishes caught to 1
        Map<Identifier, FishCaughtCounter> fishesCaught = new HashMap<>();

        for (FishProperties fish : level.registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
        {
            if(fish.hasGuideEntry())
                fishesCaught.put(U.getRlFromFp(level, fish), FishCaughtCounter.createHacked());
        }

        FishingGuideAttachment.setFishesCaught(player, fishesCaught);

        return InteractionResult.SUCCESS;
    }


}
