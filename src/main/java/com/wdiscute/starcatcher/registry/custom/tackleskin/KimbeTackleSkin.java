package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.resources.Identifier;

public class KimbeTackleSkin extends AbstractTackleSkin
{
    @Override
    public Identifier getLayerLocationId()
    {
        return Starcatcher.rl("kimbe");
    }

    @Override
    public Identifier getTexture()
    {
        return Starcatcher.rl("textures/entity/tackle/kimbe.png");
    }
}
