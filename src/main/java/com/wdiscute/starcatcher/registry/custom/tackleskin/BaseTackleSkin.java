package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.resources.Identifier;

public class BaseTackleSkin extends AbstractTackleSkin
{
    @Override
    public Identifier getLayerLocationId()
    {
        return Starcatcher.rl("base");
    }

    @Override
    public Identifier getTexture()
    {
        return Starcatcher.rl("textures/entity/tackle/base.png");
    }
}
