package com.wdiscute.starcatcher.particles;

import com.wdiscute.starcatcher.U;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class FishingBitingLavaParticles extends SingleQuadParticle
{
    private final SpriteSet sprites;

    protected FishingBitingLavaParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet)
    {
        super(level, x, y, z, spriteSet.get(0, 1));

        this.xd = 0f + U.r.nextFloat(0.2f) - 0.1f;
        this.yd = 0f + U.r.nextFloat(0.2f) + 0.1f;
        this.zd = 0f + U.r.nextFloat(0.2f) - 0.1f;

        this.quadSize = U.r.nextFloat(0.2f) + 0.05f;

        this.lifetime = 20;

        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick()
    {
        this.setSpriteFromAge(this.sprites);

        this.yd -= 0.01f;

        this.xd *= 0.95f;
        this.yd *= 0.95f;
        this.zd *= 0.95f;

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) this.remove();

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer()
    {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }


    public static class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource)
        {
            return new FishingBitingLavaParticles(clientLevel, x, y, z, this.spriteSet);
        }
    }

}
