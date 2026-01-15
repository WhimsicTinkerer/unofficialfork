package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.resources.Identifier;

public class PearlTackleSkin extends AbstractTackleSkin
{
    @Override
    public Identifier getLayerLocationId()
    {
        return Starcatcher.rl("pearl");
    }

    @Override
    public Identifier getTexture()
    {
        return Starcatcher.rl("textures/entity/tackle/pearl.png");
    }
}
