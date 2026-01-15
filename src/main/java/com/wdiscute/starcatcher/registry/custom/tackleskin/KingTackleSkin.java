package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.registry.ModSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class KingTackleSkin extends AbstractTackleSkin
{
    @Override
    public Identifier getLayerLocationId()
    {
        return Starcatcher.rl("king");
    }

    @Override
    public Identifier getTexture()
    {
        return Starcatcher.rl("textures/entity/tackle/king.png");
    }

    @Override
    public void onMissed(Player player)
    {
        super.onMissed(player);
        Vec3 p = player.position();
        player.level().playSound(null, p.x, p.y, p.z, ModSounds.KING_GRR.get(), SoundSource.NEUTRAL, 1f, 1f);
    }

    @Override
    public void onSuccessfulMinigame(Player player)
    {
        Vec3 p = player.position();
        player.level().playSound(null, p.x, p.y, p.z, ModSounds.KING_HEHEHA.get(), SoundSource.NEUTRAL, 1f, 1f);
    }

    @Override
    public void onFailedMinigame(Player player)
    {
        Vec3 p = player.position();
        player.level().playSound(null, p.x, p.y, p.z, ModSounds.KING_CRY.get(), SoundSource.NEUTRAL, 1f, 1f);
    }
}
