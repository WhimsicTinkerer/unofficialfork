package com.wdiscute.starcatcher.registry.custom.catchmodifiers;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.bob.FishingBobEntity;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class IgnoreDaytimeWeatherRestrictions extends AbstractCatchModifier
{

    @Override
    public List<FishProperties> modifyAvailablePool(List<FishProperties> ignore)
    {
        List<FishProperties> available = new ArrayList<>();
        for (FishProperties fp : instance.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
        {
            int chance = getChanceMeteorological(fp, instance, instance.rod);

            for (int i = 0; i < chance; i++)
            {
                available.add(fp);
            }
        }

        return available;
    }

    public static int getChanceMeteorological(FishProperties fp, Entity entity, ItemStack rod)
    {
        Level level = entity.level();

        if (!FishProperties.isSeasonCorrect(entity, fp)) return 0;

        if (!FishProperties.isDimensionCorrect(entity, fp)) return 0;

        if (!FishProperties.isBiomeCorrect(entity, fp)) return 0;

        if (!FishProperties.isElevationCorrect(entity, fp)) return 0;

        //fluid check
        boolean fluid = fp.wr().fluids().contains(BuiltInRegistries.FLUID.getKey(FishProperties.getSource(level.getFluidState(entity.blockPosition()).getType())));
        boolean fluidAbove = fp.wr().fluids().contains(BuiltInRegistries.FLUID.getKey(FishProperties.getSource(level.getFluidState(entity.blockPosition().above()).getType())));
        boolean fluidBelow = fp.wr().fluids().contains(BuiltInRegistries.FLUID.getKey(FishProperties.getSource(level.getFluidState(entity.blockPosition().below()).getType())));

        if (!fluid && !fluidAbove && !fluidBelow && entity instanceof FishingBobEntity)
            return 0;

        //correct bait chance bonus
        ItemStack bait = ModDataComponents.has(rod, ModDataComponents.BAIT) ? ModDataComponents.get(rod, ModDataComponents.BAIT).stack().copy() : ItemStack.EMPTY;
        if (fp.br().correctBait().contains(BuiltInRegistries.ITEM.getKey(bait.getItem())))
        {
            return fp.baseChance() + fp.br().correctBaitChanceAdded();
        }

        return fp.baseChance();
    }
}
