package com.wdiscute.starcatcher.items.cheater;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.FishCaughtCounter;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.io.network.FishCaughtPayload;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;

public class AwardOneFish extends Item
{
    public AwardOneFish()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        if(!player.isCreative()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        Map<Identifier, FishCaughtCounter> fishesCaught = FishingGuideAttachment.getFishesCaught(player);

        Optional<Holder.Reference<FishProperties>> optional = level.registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY).getRandom(level.random);

        if(optional.isPresent())
        {
            if(optional.get().is(U.rl("minecraft", "nether_star"))) return InteractionResult.PASS;
            FishProperties fp = optional.get().value();

            //todo fix this awarding repeated entries. It should check which entries the player doesnt have to award a new one instead
            fishesCaught.putIfAbsent(U.getRlFromFp(level, fp), FishCaughtCounter.createHacked());

            if(player instanceof ServerPlayer sp)
            {
                PacketDistributor.sendToPlayer(sp, new FishCaughtPayload(fp, false, 0, 0));
            }
        }

        FishingGuideAttachment.sync(player);

        return InteractionResult.SUCCESS;
    }


}
