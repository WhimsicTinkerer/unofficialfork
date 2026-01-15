package com.wdiscute.starcatcher.items;

import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.fishentity.FishEntity;
import com.wdiscute.starcatcher.guide.SettingsScreen;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SingleStackContainer;
import com.wdiscute.starcatcher.io.SizeAndWeightInstance;
import com.wdiscute.starcatcher.registry.ModEntities;
import com.wdiscute.starcatcher.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class StarcaughtBucket extends BucketItem
{
    EntityType<FishEntity> entity;

    public StarcaughtBucket(Fluid fluid)
    {
        super(
                fluid, new Item.Properties().stacksTo(16));

        entity = ModEntities.FISH.get();
    }

    // Note: checkExtraContent was removed in 1.21.11 - spawn logic should be handled elsewhere
    public void onBucketUse(@Nullable Player player, Level level, ItemStack containerStack, BlockPos pos)
    {
        if (level instanceof ServerLevel)
        {
            this.spawn((ServerLevel) level, containerStack, pos);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        }
    }

    private void spawn(ServerLevel serverLevel, ItemStack bucketedMobStack, BlockPos pos)
    {
        FishEntity fishEntity = this.entity.spawn(serverLevel, bucketedMobStack, null, pos, EntitySpawnReason.BUCKET, true, false);
        if(ModDataComponents.has(bucketedMobStack, ModDataComponents.BUCKETED_FISH))
            fishEntity.setFish(getFish(bucketedMobStack));
        else
            fishEntity.setFish(ModItems.AURORA.toStack());
    }

    private static ItemStack getFish(ItemStack bucket) {
        return ModDataComponents.getOrDefault(bucket,ModDataComponents.BUCKETED_FISH, new SingleStackContainer(ItemStack.EMPTY)).stack();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, Consumer<Component> tooltipConsumer, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipConsumer, tooltipFlag);
        ItemStack fish = getFish(stack);
        if (fish.isEmpty())
        {
            tooltipConsumer.accept(Component.translatable("tooltip.starcatcher.starcaught_bucket.creative.0").withColor(0x888888));
            tooltipConsumer.accept(Component.translatable("tooltip.starcatcher.starcaught_bucket.creative.1").withColor(0x888888));
        }
    }

    @Override
    public Component getName(ItemStack stack)
    {
        SingleStackContainer ssc = ModDataComponents.get(stack, ModDataComponents.BUCKETED_FISH);

        if (ssc == null)
            return super.getName(stack);
        else
        {
            return Component.translatable("tooltip.starcatcher.starcaught_bucket.before")
                    .append(ssc.stack().getItem().getName(stack))
                    .append(Component.translatable("tooltip.starcatcher.starcaught_bucket.after"));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new BucketTooltip(getFish(stack)));
    }

    public record BucketTooltip(ItemStack fish) implements TooltipComponent {}
}
