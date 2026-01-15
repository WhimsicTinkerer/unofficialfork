package com.wdiscute.starcatcher.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.io.ExtraComposites;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.io.attachments.FishingGuideAttachment;
import com.wdiscute.starcatcher.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record TrophyProperties(
        FishProperties fp,
        boolean alwaysShow,
        TrophyType trophyType,
        RarityProgress all,
        Map<FishProperties.Rarity, RarityProgress> progress,
        int chanceToCatch,
        boolean repeatable
)
{

    public RarityProgress getProgress(FishProperties.Rarity rarity)
    {
        return progress.getOrDefault(rarity, RarityProgress.DEFAULT);
    }

    /**
     * @deprecated use Builder instead
     */
    @Deprecated(forRemoval = true)
    public static final TrophyProperties DEFAULT = new TrophyProperties(
            FishProperties.builder().withFish(ModItems.MISSINGNO).build(),
            false,
            TrophyType.EXTRA,
            RarityProgress.DEFAULT,
            Map.of(),
            100,
            false
    );

    //region codec

    public static final Codec<TrophyProperties> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FishProperties.CODEC.fieldOf("fish_properties").forGetter(TrophyProperties::fp),
                    Codec.BOOL.fieldOf("always_show").forGetter(TrophyProperties::alwaysShow),
                    TrophyType.CODEC.fieldOf("trophy_type").forGetter(TrophyProperties::trophyType),
                    RarityProgress.CODEC.fieldOf("all").forGetter(TrophyProperties::all),
                    Codec.unboundedMap(FishProperties.Rarity.CODEC, RarityProgress.CODEC).fieldOf("progress").forGetter(TrophyProperties::progress),
                    Codec.INT.fieldOf("chance_to_catch").forGetter(TrophyProperties::chanceToCatch),
                    Codec.BOOL.fieldOf("repeatable").forGetter(TrophyProperties::repeatable)
            ).apply(instance, TrophyProperties::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrophyProperties> STREAM_CODEC = ExtraComposites.composite(
            FishProperties.STREAM_CODEC, TrophyProperties::fp,
            ByteBufCodecs.BOOL, TrophyProperties::alwaysShow,
            TrophyType.STREAM_CODEC, TrophyProperties::trophyType,
            RarityProgress.STREAM_CODEC, TrophyProperties::all,
            ByteBufCodecs.fromCodec(Codec.unboundedMap(FishProperties.Rarity.CODEC, RarityProgress.CODEC)), TrophyProperties::progress, //TODO make better ig
            ByteBufCodecs.VAR_INT, TrophyProperties::chanceToCatch,
            ByteBufCodecs.BOOL, TrophyProperties::repeatable,
            TrophyProperties::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<TrophyProperties>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final Codec<List<TrophyProperties>> LIST_CODEC = TrophyProperties.CODEC.listOf();

    //endregion

    public static Builder builder()
    {
        return new Builder();
    }

    public Holder<Item> fish()
    {
        return this.fp.catchInfo().fish();
    }

    public static class Builder
    {
        private FishProperties.Builder fp = FishProperties.builder().withFish(ModItems.MISSINGNO);
        private boolean hide_until_caught = false;
        private TrophyType trophyType = TrophyType.EXTRA;
        private RarityProgress all = RarityProgress.DEFAULT;
        private final Map<FishProperties.Rarity, RarityProgress> progressMap = new EnumMap<>(FishProperties.Rarity.class);
        private int chanceToCatch = 100;
        private boolean repeatable = false;

        private Builder()
        {
        }

        public Builder hideUntilCaught()
        {
            this.hide_until_caught = true;
            return this;
        }

        public Builder setTrophyType(TrophyType type)
        {
            this.trophyType = type;
            return this;
        }

        public Builder setAllProgress(RarityProgress progress)
        {
            this.all = progress;
            return this;
        }

        public Builder withProgress(FishProperties.Rarity rarity, RarityProgress progress)
        {
            this.progressMap.put(rarity, progress);
            return this;
        }

        public Builder setChanceToCatch(int chanceToCatch)
        {
            this.chanceToCatch = chanceToCatch;
            return this;
        }

        public Builder setRepeatable(boolean repeatable)
        {
            this.repeatable = repeatable;
            return this;
        }

        public Builder setFishProperties(FishProperties.Builder builder)
        {
            this.fp = builder;
            return this;
        }

        public Builder setFish(Holder<Item> fish)
        {
            this.fp.withFish(fish);
            return this;
        }

        public TrophyProperties build()
        {
            return new TrophyProperties(
                    fp.build(),
                    hide_until_caught,
                    trophyType,
                    all,
                    progressMap,
                    chanceToCatch,
                    repeatable
            );
        }
    }

    public record RarityProgress(int total, int unique)
    {
        public static final Codec<RarityProgress> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("total").forGetter(RarityProgress::total),
                        Codec.INT.fieldOf("unique").forGetter(RarityProgress::unique)
                ).apply(instance, RarityProgress::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RarityProgress> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, RarityProgress::total,
                ByteBufCodecs.VAR_INT, RarityProgress::unique,
                RarityProgress::new
        );

        public static final RarityProgress DEFAULT = new RarityProgress(0, 0);

        public static RarityProgress fromAttachment(Player player){
            return new RarityProgress(0, FishingGuideAttachment.getTrophiesCaught(player).size());
        }
    }

    public enum TrophyType implements StringRepresentable
    {
        TROPHY("trophy"),
        SECRET("secret"),
        EXTRA("extra");

        public static final Codec<TrophyType> CODEC = StringRepresentable.fromEnum(TrophyType::values);
        public static final StreamCodec<FriendlyByteBuf, TrophyType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(TrophyType.class);
        private final String key;

        TrophyType(String key)
        {
            this.key = key;
        }

        public @NotNull String getSerializedName()
        {
            return this.key;
        }
    }
}
