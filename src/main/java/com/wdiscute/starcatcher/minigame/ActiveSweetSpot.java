package com.wdiscute.starcatcher.minigame;

import com.mojang.logging.LogUtils;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.registry.custom.sweetspotbehaviour.AbstractSweetSpotBehaviour;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ActiveSweetSpot
{
    //from ss
    public final AbstractSweetSpotBehaviour behaviour;
    public final FishProperties.SweetSpot baseSS;
    public int thickness;
    public Identifier texture;
    public int reward;
    public int particleColor;

    //from minigame screen/rod
    public final FishingMinigameScreen instance;
    public final ItemStack bobber;
    public final ItemStack bait;
    public final ItemStack hook;

    //from fp
    public boolean isFlip;
    public float vanishingRate;
    public float movingRate;

    //active sweet spot tracking
    public float alpha;
    public float pos;
    public int currentRotation;

    public boolean removed = false;
    public boolean shouldSudokuOnVanish = false;

    // For use with modifiers, map an id with some data
    public Map<Integer, Object> extraData = new HashMap<>();

    public ActiveSweetSpot(FishingMinigameScreen instance, FishProperties.SweetSpot ss, ItemStack bobber, ItemStack bait, ItemStack hook)
    {
        //get sweet spot type from rl
        Optional<Supplier<? extends AbstractSweetSpotBehaviour>> behaviour = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Starcatcher.SWEET_SPOT_BEHAVIOUR).getOptional(ss.sweetSpotType());

        //if sweet spot type is registered then continue, otherwise set as removed
        if(behaviour.isPresent())
            this.behaviour = behaviour.get().get();
        else
        {
            this.behaviour = null;
            LogUtils.getLogger().error("The sweet-spot type {} is not registered, as such the sweet-spot has not been added", ss.sweetSpotType());
            removed = true;
        }

        this.instance = instance;

        this.baseSS = ss;
        this.texture = ss.texturePath();
        this.thickness = ss.size();
        this.reward = ss.reward();
        this.particleColor = ss.particleColor();

        this.bobber = bobber;
        this.bait = bait;
        this.hook = hook;

        this.isFlip = ss.isFlip();
        this.vanishingRate = ss.vanishingRate();
        this.movingRate = ss.movingRate();

        currentRotation = 1;

        this.alpha = 1;
    }

    public ActiveSweetSpot(FishingMinigameScreen instance, FishProperties.SweetSpot ss)
    {
        this(instance, ss, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public boolean isHoveredOver(){
        return FishingMinigameScreen.doDegreesOverlapWithLeeway(instance.getPointerPosPrecise(), this.pos, thickness);
    }

}
