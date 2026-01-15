package com.wdiscute.starcatcher.fishentity;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SingleStackContainer;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FishEntity extends AbstractFish
{
    public FishEntity(EntityType<? extends FishEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    private boolean shouldDropItem = true;

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    public @Nullable ItemStack getPickResult()
    {
        return getBodyArmorItem();
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0F);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(getBodyArmorItem().isEmpty() && !level().isClientSide())
        {
            shouldDropItem = false;
            List<FishProperties> available = new ArrayList<>();

            for (FishProperties fp : level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
            {
                if (FishProperties.getChance(fp, this, ModItems.ROD.toStack()) > 0 && fp.catchInfo().fish().is(StarcatcherTags.BUCKETABLE_FISHES)) available.add(fp);
            }

            if(available.isEmpty())
                kill((ServerLevel) level());
            else
            {
                FishProperties fp = available.get(U.r.nextInt(available.size() - 1));
                ItemStack is = new ItemStack(fp.catchInfo().fish());
                setBodyArmorItem(is);
            }
        }
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource)
    {
        if(shouldDropItem)
            super.dropAllDeathLoot(p_level, damageSource);
    }

    public void setFish(ItemStack is)
    {
        setBodyArmorItem(is);
        shouldDropItem = true;
        setCustomName(is.getDisplayName());
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        ItemStack is = new ItemStack(ModItems.STARCAUGHT_BUCKET.get());
        ModDataComponents.set(is, ModDataComponents.BUCKETED_FISH, new SingleStackContainer(getBodyArmorItem().copy()));
        return is;
    }
}
