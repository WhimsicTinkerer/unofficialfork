package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.resources.Identifier;

public class ClearTackleSkin extends AbstractTackleSkin
{
    @Override
    public Identifier getLayerLocationId()
    {
        return Starcatcher.rl("clear");
    }

    @Override
    public Identifier getTexture()
    {
        return Starcatcher.rl("textures/entity/tackle/clear.png");
    }
}
