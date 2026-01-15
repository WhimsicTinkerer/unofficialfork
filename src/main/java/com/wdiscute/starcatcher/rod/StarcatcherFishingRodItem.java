package com.wdiscute.starcatcher.rod;

import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.bob.FishingBobEntity;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SingleStackContainer;
import com.wdiscute.starcatcher.io.attachments.FishingBobAttachment;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ModTackleSkins;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarcatcherFishingRodItem extends Item implements MenuProvider
{
    public StarcatcherFishingRodItem()
    {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .fireResistant()
                .stacksTo(1)
                .component(ModDataComponents.BOBBER.get(), new SingleStackContainer(new ItemStack(ModItems.BOBBER.get())))
                .component(ModDataComponents.BAIT.get(), SingleStackContainer.EMPTY)
                .component(ModDataComponents.HOOK.get(), new SingleStackContainer(new ItemStack(ModItems.HOOK.get())))
        );
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        ItemStack is = player.getItemInHand(hand);

        if (!is.is(StarcatcherTags.RODS))
            return InteractionResult.PASS;

        FishingBobAttachment fishingBobAttachment = ModDataAttachments.get(player, ModDataAttachments.FISHING_BOB.get());
        if (player.isCrouching() && fishingBobAttachment.isEmpty())
        {
            player.openMenu(this);
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;


        if (fishingBobAttachment.isEmpty())
        {
            ModTackleSkins.get(player.level(), player.getItemInHand(hand)).onCast(player);

            if (level instanceof ServerLevel)
            {
                //TODO ADD CUSTOM STAT FOR NUMBER OF FISHES CAUGHT TOTAL ON STAT SCREEN

                Entity entity = new FishingBobEntity(level, player, is);
                level.addFreshEntity(entity);
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(player.getX(), entity.getEyeY(), player.getZ()));

                fishingBobAttachment.setUuid(player, entity.getUUID());
                if(ModDataComponents.has(is, ModDataComponents.TACKLE_SKIN))
                    ModDataAttachments.set(entity, ModDataAttachments.TACKLE_SKIN.get(), ModDataComponents.get(is, ModDataComponents.TACKLE_SKIN));
            }
        }
        else
        {

            List<Entity> entities = level.getEntities(null, new AABB(-25, -65, -25, 25, 65, 25).move(player.position()));

            for (Entity entity : entities)
            {
                if (entity.getUUID().equals(fishingBobAttachment.getUuid()))
                {
                    if (entity instanceof FishingBobEntity fbe && !fbe.checkBiting())
                    {
                        ModTackleSkins.get(player.level(), player.getItemInHand(hand)).onRetrieve(player);

                        fbe.kill((ServerLevel) level);
                        ModDataAttachments.remove(player, ModDataAttachments.FISHING_BOB.get());
                    }
                }
            }

        }


        return InteractionResult.SUCCESS;
    }


    // Note: hasCraftingRemainingItem and getCraftingRemainingItem removed in 1.21.11
    // Crafting remainder is now handled via Item.Properties.craftRemainder()
    public boolean hasCraftingRemainder(ItemStack stack)
    {
        return true;
    }

    public ItemStack getCraftingRemainder(ItemStack itemStack)
    {
        return itemStack.copy();
    }

    @Override
    public Component getDisplayName()
    {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
    {
        if (player.getMainHandItem().is(StarcatcherTags.RODS))
            return new FishingRodMenu(i, inventory, player.getMainHandItem());
        else
            return new FishingRodMenu(i, inventory, player.getOffhandItem());
    }
}

