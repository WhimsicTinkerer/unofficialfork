package com.wdiscute.starcatcher.guide;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class FishingGuideItem extends Item
{
    public FishingGuideItem(Item.Properties props)
    {
        super(props.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        if(level.isClientSide() && FMLEnvironment.getDist() == Dist.CLIENT)
        {
            ClientHandler.openScreen();
        }
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
        return InteractionResult.SUCCESS;
    }

    // Inner class to isolate client code - only loaded when DistExecutor runs on CLIENT
    private static class ClientHandler
    {
        static void openScreen()
        {
            net.minecraft.client.Minecraft.getInstance().setScreen(new FishingGuideScreen());
        }
    }
}
