package com.wdiscute.starcatcher.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.compat.FTBTeamsCompat;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.io.network.FishCaughtPayload;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public record FishCaughtCounter(
        int count,
        int fastestTicks,
        float averageTicks,
        int size,
        int weight,
        boolean caughtGolden,
        boolean perfectCatch,
        boolean hasGuideNotification
)
{

    public static final Codec<FishCaughtCounter> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("count", 0).forGetter(FishCaughtCounter::count),
                    Codec.INT.optionalFieldOf("fastest_ticks", 0).forGetter(FishCaughtCounter::fastestTicks),
                    Codec.FLOAT.optionalFieldOf("average_ticks", 0.0f).forGetter(FishCaughtCounter::averageTicks),
                    Codec.INT.optionalFieldOf("best_size", 0).forGetter(FishCaughtCounter::size),
                    Codec.INT.optionalFieldOf("best_weight", 0).forGetter(FishCaughtCounter::weight),
                    Codec.BOOL.optionalFieldOf("caught_golden", false).forGetter(FishCaughtCounter::caughtGolden),
                    Codec.BOOL.optionalFieldOf("perfect_catch", false).forGetter(FishCaughtCounter::perfectCatch),
                    Codec.BOOL.optionalFieldOf("has_guide_notification", false).forGetter(FishCaughtCounter::hasGuideNotification)

            ).apply(instance, FishCaughtCounter::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FishCaughtCounter> STREAM_CODEC = ExtraComposites.composite(
            ByteBufCodecs.VAR_INT, FishCaughtCounter::count,
            ByteBufCodecs.VAR_INT, FishCaughtCounter::fastestTicks,
            ByteBufCodecs.FLOAT, FishCaughtCounter::averageTicks,
            ByteBufCodecs.INT, FishCaughtCounter::size,
            ByteBufCodecs.INT, FishCaughtCounter::weight,
            ByteBufCodecs.BOOL, FishCaughtCounter::caughtGolden,
            ByteBufCodecs.BOOL, FishCaughtCounter::perfectCatch,
            ByteBufCodecs.BOOL, FishCaughtCounter::hasGuideNotification,

            FishCaughtCounter::new
    );

    public static FishCaughtCounter get(Player player, FishProperties loc)
    {
        return get(player, U.getRlFromFp(player.level(), loc));
    }

    public static FishCaughtCounter get(Player player, Identifier loc)
    {
        return FishingGuideAttachment.getFishesCaught(player).get(loc);
    }

    public static FishCaughtCounter createHacked()
    {
        return new FishCaughtCounter(999999, 0, 0, 0, 0, false, false, true);
    }

    public FishCaughtCounter removeNotification()
    {
        return new FishCaughtCounter(this.count, this.fastestTicks, this.averageTicks, this.size, this.weight, this.caughtGolden, perfectCatch, false);
    }


    public static FishCaughtCounter create(int ticks, int size, int weight, boolean perfectCatch)
    {
        return new FishCaughtCounter(1, ticks, (float) ticks, size, weight, false, perfectCatch, true);
    }

    public FishCaughtCounter getUpdated(int ticks, int size, int weight, boolean perfectCatch)
    {
        int fastestToSave = Math.min(this.fastestTicks, ticks);
        float averageToSave = (this.averageTicks * this.count + ticks) / (this.count + 1);
        int countToSave = this.count;
        boolean perfect = perfectCatch || this.perfectCatch;

        //if cheated in, fixes trackers
        if (this.fastestTicks == 0) fastestToSave = ticks;
        if (this.averageTicks == 0) averageToSave = ticks;
        if (this.count == 999999) countToSave = 0;

        int sizeToSave = Math.max(size, this.size);
        int weightToSave = Math.max(weight, this.weight);

        return new FishCaughtCounter(
                countToSave + 1,
                fastestToSave,
                averageToSave, sizeToSave, weightToSave,
                this.caughtGolden,
                perfect,
                true);
    }

    public static void awardFishCaughtCounter(FishProperties fpCaught, Player player, int ticks, int size, int weight, boolean perfectCatch, boolean awardToTeam)
    {
        //ftb teams compat to share fishes caught to team, does not share size and weight
        if (ModList.get().isLoaded("ftbteams") && awardToTeam && Config.ENABLE_FTB_TEAM_SHARING.get())
        {
            FTBTeamsCompat.awardToTeam(player, fpCaught);
            return;
        }

        Map<Identifier, FishCaughtCounter> fishesCaught = FishingGuideAttachment.getFishesCaught(player);
        Identifier loc = player.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY).getKey(fpCaught);
        FishCaughtCounter fishCaughtCounter = fishesCaught.get(loc);

        boolean newFish = fishCaughtCounter == null;

        if (newFish)
            fishCaughtCounter = FishCaughtCounter.create(ticks, size, weight, perfectCatch);
        else
            fishCaughtCounter = fishCaughtCounter.getUpdated(ticks, size, weight, perfectCatch);

        fishesCaught.put(loc, fishCaughtCounter);

        //send packet to client to display message above exp bar and fish caught toast, unless it alwaysSpawnEntity() (where sw and caught doesn't make sense)
        if (!fpCaught.catchInfo().alwaysSpawnEntity())
            PacketDistributor.sendToPlayer(((ServerPlayer) player), new FishCaughtPayload(fpCaught, newFish, size, weight));

        FishingGuideAttachment.setFishesCaught(player, fishesCaught);
    }

}
