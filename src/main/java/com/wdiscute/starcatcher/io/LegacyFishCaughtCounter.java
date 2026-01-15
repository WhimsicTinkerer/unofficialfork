package com.wdiscute.starcatcher.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@Deprecated
public record LegacyFishCaughtCounter(
        Identifier fp,
        int count,
        int fastestTicks,
        float averageTicks,
        int size,
        int weight,
        boolean caughtGolden,
        boolean perfectCatch
)
{

    public static final Codec<LegacyFishCaughtCounter> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("fps").forGetter(LegacyFishCaughtCounter::fp),
                    Codec.INT.optionalFieldOf("count", 0).forGetter(LegacyFishCaughtCounter::count),
                    Codec.INT.optionalFieldOf("fastest_ticks", 0).forGetter(LegacyFishCaughtCounter::fastestTicks),
                    Codec.FLOAT.optionalFieldOf("average_ticks", 0.0f).forGetter(LegacyFishCaughtCounter::averageTicks),
                    Codec.INT.optionalFieldOf("best_size", 0).forGetter(LegacyFishCaughtCounter::size),
                    Codec.INT.optionalFieldOf("best_weight", 0).forGetter(LegacyFishCaughtCounter::weight),
                    Codec.BOOL.optionalFieldOf("caught_golden", false).forGetter(LegacyFishCaughtCounter::caughtGolden),
                    Codec.BOOL.optionalFieldOf("perfect_catch", false).forGetter(LegacyFishCaughtCounter::caughtGolden)
            ).apply(instance, LegacyFishCaughtCounter::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, LegacyFishCaughtCounter> STREAM_CODEC = ExtraComposites.composite(
            Identifier.STREAM_CODEC, LegacyFishCaughtCounter::fp,
            ByteBufCodecs.VAR_INT, LegacyFishCaughtCounter::count,
            ByteBufCodecs.VAR_INT, LegacyFishCaughtCounter::fastestTicks,
            ByteBufCodecs.FLOAT, LegacyFishCaughtCounter::averageTicks,
            ByteBufCodecs.INT, LegacyFishCaughtCounter::size,
            ByteBufCodecs.INT, LegacyFishCaughtCounter::weight,
            ByteBufCodecs.BOOL, LegacyFishCaughtCounter::caughtGolden,
            ByteBufCodecs.BOOL, LegacyFishCaughtCounter::perfectCatch,
            LegacyFishCaughtCounter::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<LegacyFishCaughtCounter>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());


    public static final Codec<List<LegacyFishCaughtCounter>> LIST_CODEC = LegacyFishCaughtCounter.CODEC.listOf();

    public FishCaughtCounter covert(){
        return new FishCaughtCounter(count, fastestTicks, averageTicks, size, weight, caughtGolden, perfectCatch, false);
    }

    public static void awardFishCaughtCounter(FishProperties fpCaught, Player player, int ticks, int size, int weight, boolean perfectCatch, boolean awardToTeam)
    {
        System.out.println("This is a good place for a secret message");
    }

}
