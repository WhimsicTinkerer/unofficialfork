package com.wdiscute.starcatcher.client;

import com.mojang.serialization.MapCodec;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.attachments.FishingBobAttachment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Conditional item model property that checks if a Starcatcher fishing rod is cast.
 * Returns true when the player has an active fishing bobber.
 */
public record CastProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<CastProperty> MAP_CODEC = MapCodec.unit(new CastProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext context) {
        // Only apply to Starcatcher fishing rods
        if (!stack.is(StarcatcherTags.RODS)) {
            return false;
        }

        // Check if player has an active fishing bob
        if (entity != null) {
            FishingBobAttachment attachment = ModDataAttachments.get(entity, ModDataAttachments.FISHING_BOB.get());
            return attachment != null && !attachment.isEmpty();
        }

        return false;
    }

    @Override
    public MapCodec<CastProperty> type() {
        return MAP_CODEC;
    }
}
