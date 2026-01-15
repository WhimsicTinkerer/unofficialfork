package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.wdiscute.starcatcher.minigame.ActiveSweetSpot;

public class HeavyHookModifier extends AbstractMinigameModifier
{
    float rate;

    public HeavyHookModifier(float speedDividedByX)
    {
        this.rate = speedDividedByX;
    }

    @Override
    public ActiveSweetSpot onSpotAdded(ActiveSweetSpot ass)
    {
        super.onSpotAdded(ass);
        ass.movingRate /= rate;
        return ass;
    }
}
