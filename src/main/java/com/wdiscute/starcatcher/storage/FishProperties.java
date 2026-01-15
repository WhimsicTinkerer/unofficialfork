package com.wdiscute.starcatcher.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.bob.FishingBobEntity;
import com.wdiscute.starcatcher.compat.EclipticSeasonsCompat;
import com.wdiscute.starcatcher.compat.SereneSeasonsCompat;
import com.wdiscute.starcatcher.compat.TerraFirmaCraftSeasonsCompat;
import com.wdiscute.starcatcher.io.ExtraComposites;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.ModMinigameModifiers;
import com.wdiscute.starcatcher.registry.custom.sweetspotbehaviour.ModSweetSpotsBehaviour;
import com.wdiscute.starcatcher.registry.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//      <><|    <- fish
public record FishProperties(
        CatchInfo catchInfo,
        int baseChance,
        SizeAndWeight sizeWeight,
        Rarity rarity,
        WorldRestrictions wr,
        BaitRestrictions br,
        Difficulty dif,
        Daytime daytime,
        Weather weather,
        boolean skipMinigame,
        boolean hasGuideEntry
)
{
    public static final Codec<FishProperties> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CatchInfo.CODEC.fieldOf("catch_info").forGetter(FishProperties::catchInfo),
                    Codec.INT.fieldOf("base_chance").forGetter(FishProperties::baseChance),
                    SizeAndWeight.CODEC.fieldOf("size_and_weight").forGetter(FishProperties::sizeWeight),
                    Rarity.CODEC.fieldOf("rarity").forGetter(FishProperties::rarity),
                    WorldRestrictions.CODEC.fieldOf("world_restrictions").forGetter(FishProperties::wr),
                    BaitRestrictions.CODEC.fieldOf("bait_restrictions").forGetter(FishProperties::br),
                    Difficulty.CODEC.fieldOf("difficulty").forGetter(FishProperties::dif),
                    Daytime.CODEC.fieldOf("daytime").forGetter(FishProperties::daytime),
                    Weather.CODEC.fieldOf("weather").forGetter(FishProperties::weather),
                    Codec.BOOL.fieldOf("skips_minigame").forGetter(FishProperties::skipMinigame),
                    Codec.BOOL.fieldOf("has_guide_entry").forGetter(FishProperties::hasGuideEntry)

            ).apply(instance, FishProperties::new)
    );

    public static final Codec<List<FishProperties>> LIST_CODEC = FishProperties.CODEC.listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, FishProperties> STREAM_CODEC = ExtraComposites.composite(
            CatchInfo.STREAM_CODEC, FishProperties::catchInfo,
            ByteBufCodecs.VAR_INT, FishProperties::baseChance,
            SizeAndWeight.STREAM_CODEC, FishProperties::sizeWeight,
            Rarity.STREAM_CODEC, FishProperties::rarity,
            WorldRestrictions.STREAM_CODEC, FishProperties::wr,
            BaitRestrictions.STREAM_CODEC, FishProperties::br,
            Difficulty.STREAM_CODEC, FishProperties::dif,
            Daytime.STREAM_CODEC, FishProperties::daytime,
            Weather.STREAM_CODEC, FishProperties::weather,
            ByteBufCodecs.BOOL, FishProperties::skipMinigame,
            ByteBufCodecs.BOOL, FishProperties::hasGuideEntry,
            FishProperties::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FishProperties>> STREAM_CODEC_LIST = STREAM_CODEC.apply(ByteBufCodecs.list());

    public Identifier toLoc(Level level){
        return U.getRlFromFp(level, this);
    }

    /**
     * @deprecated use Builder instead
     */
    @Deprecated(forRemoval = true)
    public static final FishProperties DEFAULT = new FishProperties(
            CatchInfo.DEFAULT,
            5,
            SizeAndWeight.DEFAULT,
            Rarity.COMMON,
            WorldRestrictions.DEFAULT,
            BaitRestrictions.DEFAULT,
            Difficulty.EASY,
            Daytime.ALL,
            Weather.ALL,
            false,
            true
    );

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private CatchInfo.Builder catchInfo = new CatchInfo.Builder();
        private int baseChance = 5;

        private SizeAndWeight sw = SizeAndWeight.DEFAULT;
        private Rarity rarity = Rarity.COMMON;
        private WorldRestrictions wr = WorldRestrictions.DEFAULT;
        private BaitRestrictions br = BaitRestrictions.DEFAULT;
        private Difficulty dif = Difficulty.EASY;
        private Daytime daytime = Daytime.ALL;
        private Weather weather = Weather.ALL;
        private boolean skipMinigame = false;
        private boolean hasGuideEntry = true;

        public Builder withCatchInfo(CatchInfo.Builder builder)
        {
            this.catchInfo = builder;
            return this;
        }

        public Builder withFish(Holder<Item> fish)
        {
            this.catchInfo.withFish(fish);
            return this;
        }

        public Builder withTreasure(Holder<Item> treasure)
        {
            this.catchInfo.treasure = treasure;
            return this;
        }

        public Builder withBucketedFish(Holder<Item> bucketedFish)
        {
            this.catchInfo.withBucketedFish(bucketedFish);
            return this;
        }

        public Builder withEntityToSpawn(Holder<EntityType<?>> entity)
        {
            this.catchInfo.withEntityToSpawn(entity);
            return this;
        }

        public Builder withAlwaysSpawnEntity(boolean alwaysSpawnEntity)
        {
            this.catchInfo.withAlwaysSpawnEntity(alwaysSpawnEntity);
            return this;
        }

        public Builder withItemToOverrideWith(Holder<Item> itemToOverrideWith)
        {
            this.catchInfo.withOverrideMinigameWith(itemToOverrideWith);
            return this;
        }

        public Builder withBaseChance(int baseChance)
        {
            this.baseChance = baseChance;
            return this;
        }

        public Builder withSizeAndWeight(SizeAndWeight sizeAndWeight)
        {
            this.sw = sizeAndWeight;
            return this;
        }

        public Builder withRarity(Rarity rarity)
        {
            this.rarity = rarity;
            return this;
        }

        public Builder withWorldRestrictions(WorldRestrictions wr)
        {
            this.wr = wr;
            return this;
        }

        public Builder withBaitRestrictions(BaitRestrictions br)
        {
            this.br = br;
            return this;
        }

        public Builder withDifficulty(Difficulty dif)
        {
            this.dif = dif;
            return this;
        }

        public Builder withDaytime(Daytime daytime)
        {
            this.daytime = daytime;
            return this;
        }

        public Builder withWeather(Weather weather)
        {
            this.weather = weather;
            return this;
        }

        public Builder withSkipMinigame(boolean skipMinigame)
        {
            this.skipMinigame = skipMinigame;
            return this;
        }

        public Builder withHasGuideEntry(boolean hasGuideEntry)
        {
            this.hasGuideEntry = hasGuideEntry;
            return this;
        }

        public Builder withSeasons(WorldRestrictions.Seasons... seasons)
        {
            this.wr = this.wr.withSeasons(seasons);
            return this;
        }

        public FishProperties build()
        {
            return new FishProperties(
                    catchInfo.build(),
                    baseChance,
                    sw,
                    rarity,
                    wr,
                    br,
                    dif,
                    daytime,
                    weather,
                    skipMinigame,
                    hasGuideEntry
            );
        }
    }

    //region CatchInfo
    public record CatchInfo(
            Holder<Item> fish,
            Holder<Item> bucketedFish,
            Holder<EntityType<?>> entityToSpawn,
            boolean alwaysSpawnEntity,
            Holder<Item> overrideMinigameWith,
            Holder<Item> treasure
    )
    {
        public static final Codec<CatchInfo> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(CatchInfo::fish),
                        BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("fish_bucket").forGetter(CatchInfo::bucketedFish),
                        BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("entity").forGetter(CatchInfo::entityToSpawn),
                        Codec.BOOL.fieldOf("always_spawn_entity").forGetter(CatchInfo::alwaysSpawnEntity),
                        BuiltInRegistries.ITEM.holderByNameCodec().optionalFieldOf("override_minigame_item", ModItems.MISSINGNO).forGetter(CatchInfo::overrideMinigameWith),
                        BuiltInRegistries.ITEM.holderByNameCodec().optionalFieldOf("treasure", ModItems.MISSINGNO).forGetter(CatchInfo::treasure)
                ).apply(instance, CatchInfo::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CatchInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(Registries.ITEM), CatchInfo::fish,
                ByteBufCodecs.holderRegistry(Registries.ITEM), CatchInfo::bucketedFish,
                ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE), CatchInfo::entityToSpawn,
                ByteBufCodecs.BOOL, CatchInfo::alwaysSpawnEntity,
                ByteBufCodecs.holderRegistry(Registries.ITEM), CatchInfo::overrideMinigameWith,
                ByteBufCodecs.holderRegistry(Registries.ITEM), CatchInfo::treasure,
                CatchInfo::new
        );

        public static final CatchInfo DEFAULT = new CatchInfo(
                ModItems.MISSINGNO,
                ModItems.MISSINGNO,
                //cant use entity reference as its not registered for the psf
                U.holderEntity("starcatcher", "fish"),
                false,
                ModItems.MISSINGNO,
                ModItems.WATERLOGGED_SATCHEL
        );

        public CatchInfo withItemToOverrideWith(Holder<Item> itemToOverrideWith)
        {
            return new CatchInfo(this.fish, this.bucketedFish, this.entityToSpawn, alwaysSpawnEntity, itemToOverrideWith, this.treasure);
        }

        public static class Builder
        {
            private Holder<Item> fish = ModItems.MISSINGNO;
            private Holder<Item> bucketedFish = ModItems.MISSINGNO;
            private Holder<EntityType<?>> entityToSpawn = U.holderEntity("starcatcher", "fish");
            private boolean alwaysSpawnEntity = false;
            private Holder<Item> itemToOverrideWith = ModItems.MISSINGNO;
            private Holder<Item> treasure = ModItems.WATERLOGGED_SATCHEL;

            public Builder withFish(Holder<Item> fish)
            {
                this.fish = fish;
                return this;
            }

            public Builder withBucketedFish(Holder<Item> bucketedFish)
            {
                this.bucketedFish = bucketedFish;
                return this;
            }

            public Builder withEntityToSpawn(Holder<EntityType<?>> entityToSpawn)
            {
                this.entityToSpawn = entityToSpawn;
                return this;
            }

            public Builder withAlwaysSpawnEntity(boolean alwaysSpawnEntity)
            {
                this.alwaysSpawnEntity = alwaysSpawnEntity;
                return this;
            }

            public Builder withOverrideMinigameWith(Holder<Item> itemToOverrideWith)
            {
                this.itemToOverrideWith = itemToOverrideWith;
                return this;
            }

            public CatchInfo build()
            {
                return new CatchInfo(fish, bucketedFish, entityToSpawn, alwaysSpawnEntity, itemToOverrideWith, treasure);
            }
        }
    }

    //endregion CatchInfo


    //region bait

    public record BaitRestrictions(
            List<Identifier> correctBait,
            boolean consumesBait,
            int correctBaitChanceAdded
    )
    {
        public static final Codec<BaitRestrictions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.list(Identifier.CODEC).fieldOf("correct_baits").forGetter(BaitRestrictions::correctBait),
                        Codec.BOOL.fieldOf("consumes_bait").forGetter(BaitRestrictions::consumesBait),
                        Codec.INT.fieldOf("correct_bait_chance_added").forGetter(BaitRestrictions::correctBaitChanceAdded)
                ).apply(instance, BaitRestrictions::new));


        public static final StreamCodec<ByteBuf, BaitRestrictions> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), BaitRestrictions::correctBait,
                ByteBufCodecs.BOOL, BaitRestrictions::consumesBait,
                ByteBufCodecs.INT, BaitRestrictions::correctBaitChanceAdded,
                BaitRestrictions::new
        );

        public static final BaitRestrictions DEFAULT = new BaitRestrictions(
                List.of(),
                true,
                0);

        public static final BaitRestrictions FISH_OF_THIEVES = new BaitRestrictions(
                List.of(rl("fishofthieves", "earthworms"), rl("fishofthieves", "grubs"), rl("fishofthieves", "leeches")),
                true,
                20);

        public static final BaitRestrictions CHERRY_BAIT = new BaitRestrictions(
                List.of(ModItems.CHERRY_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions LUSH_BAIT = new BaitRestrictions(
                List.of(ModItems.LUSH_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions SCULK_BAIT = new BaitRestrictions(
                List.of(ModItems.SCULK_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions DRIPSTONE_BAIT = new BaitRestrictions(
                List.of(ModItems.DRIPSTONE_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions MURKWATER_BAIT = new BaitRestrictions(
                List.of(ModItems.MURKWATER_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions LEGENDARY_BAIT = new BaitRestrictions(
                List.of(ModItems.LEGENDARY_BAIT.getId()),
                true,
                15);

        public static final BaitRestrictions LEGENDARY_BAIT_VOIDBITER = new BaitRestrictions(
                List.of(ModItems.LEGENDARY_BAIT.getId()),
                true,
                50);

        public BaitRestrictions withCorrectBait(Identifier... correctBait)
        {
            return new BaitRestrictions(List.of(correctBait), this.consumesBait, this.correctBaitChanceAdded);
        }

        public BaitRestrictions withConsumesBait(boolean consumesBait)
        {
            return new BaitRestrictions(this.correctBait, consumesBait, this.correctBaitChanceAdded);
        }

        public BaitRestrictions withCorrectBaitChanceAdded(int correctBaitChanceAdded)
        {
            return new BaitRestrictions(this.correctBait, consumesBait, correctBaitChanceAdded);
        }
    }

    //endregion bait

    //region world
    public record WorldRestrictions(
            List<Identifier> dims,
            List<Identifier> dimsBlacklist,
            List<Identifier> biomes,
            List<Identifier> biomesTags,
            List<Identifier> biomesBlacklist,
            List<Identifier> biomesBlacklistTags,
            List<Identifier> fluids,
            List<Seasons> seasons,
            int mustBeCaughtBelowY,
            int mustBeCaughtAboveY
    )
    {
        public enum Seasons implements StringRepresentable
        {
            ALL("all"),

            SPRING("spring"),
            EARLY_SPRING("early_spring"),
            MID_SPRING("mid_spring"),
            LATE_SPRING("late_spring"),

            SUMMER("summer"),
            EARLY_SUMMER("early_summer"),
            MID_SUMMER("mid_summer"),
            LATE_SUMMER("late_summer"),

            AUTUMN("autumn"),
            EARLY_AUTUMN("early_autumn"),
            MID_AUTUMN("mid_autumn"),
            LATE_AUTUMN("late_autumn"),

            WINTER("winter"),
            EARLY_WINTER("early_winter"),
            MID_WINTER("mid_winter"),
            LATE_WINTER("late_winter");

            public static final Codec<FishProperties.WorldRestrictions.Seasons> CODEC = StringRepresentable.fromEnum(FishProperties.WorldRestrictions.Seasons::values);
            public static final Codec<List<FishProperties.WorldRestrictions.Seasons>> LIST_CODEC = Seasons.CODEC.listOf();
            public static final StreamCodec<RegistryFriendlyByteBuf, FishProperties.WorldRestrictions.Seasons> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(FishProperties.WorldRestrictions.Seasons.class);
            public static final StreamCodec<RegistryFriendlyByteBuf, List<FishProperties.WorldRestrictions.Seasons>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
            private final String key;

            Seasons(String key)
            {
                this.key = key;
            }

            public String getSerializedName()
            {
                return this.key;
            }

        }


        public static final WorldRestrictions DEFAULT = new WorldRestrictions(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(Identifier.withDefaultNamespace("water")),
                List.of(Seasons.ALL),
                Integer.MAX_VALUE,
                Integer.MIN_VALUE);

        public static final Codec<WorldRestrictions> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.list(Identifier.CODEC).fieldOf("dimensions").forGetter(WorldRestrictions::dims),
                        Codec.list(Identifier.CODEC).fieldOf("dimensions_blacklist").forGetter(WorldRestrictions::dimsBlacklist),
                        Codec.list(Identifier.CODEC).fieldOf("biomes").forGetter(WorldRestrictions::biomes),
                        Codec.list(Identifier.CODEC).fieldOf("biomes_tags").forGetter(WorldRestrictions::biomesTags),
                        Codec.list(Identifier.CODEC).fieldOf("biomes_blacklist").forGetter(WorldRestrictions::biomesBlacklist),
                        Codec.list(Identifier.CODEC).fieldOf("biomes_blacklist_tags").forGetter(WorldRestrictions::biomesBlacklistTags),
                        Codec.list(Identifier.CODEC).fieldOf("fluids").forGetter(WorldRestrictions::fluids),
                        Seasons.LIST_CODEC.fieldOf("seasons").forGetter(WorldRestrictions::seasons),
                        Codec.INT.fieldOf("below_y").forGetter(WorldRestrictions::mustBeCaughtBelowY),
                        Codec.INT.fieldOf("above_y").forGetter(WorldRestrictions::mustBeCaughtAboveY)
                ).apply(instance, WorldRestrictions::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WorldRestrictions> STREAM_CODEC = ExtraComposites.composite(
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::dims,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::dimsBlacklist,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::biomes,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::biomesTags,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::biomesBlacklist,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::biomesBlacklistTags,
                ByteBufCodecs.fromCodec(Codec.list(Identifier.CODEC)), WorldRestrictions::fluids,
                Seasons.LIST_STREAM_CODEC, WorldRestrictions::seasons,
                ByteBufCodecs.VAR_INT, WorldRestrictions::mustBeCaughtBelowY,
                ByteBufCodecs.VAR_INT, WorldRestrictions::mustBeCaughtAboveY,
                WorldRestrictions::new
        );

        public static final WorldRestrictions OVERWORLD =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier());

        public static final WorldRestrictions OVERWORLD_LUSH_CAVES =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomes(Biomes.LUSH_CAVES.identifier())
                        .withMustBeCaughtBelowY(50);

        public static final WorldRestrictions OVERWORLD_STONE_CAVES =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withMustBeCaughtBelowY(50)
                        .withMustBeCaughtAboveY(0);

        public static final WorldRestrictions OVERWORLD_DEEPSLATE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withMustBeCaughtBelowY(0);

        public static final WorldRestrictions OVERWORLD_DRIPSTONE_CAVES =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomes(Biomes.DRIPSTONE_CAVES.identifier())
                        .withMustBeCaughtBelowY(50)
                        .withMustBeCaughtAboveY(0);

        public static final WorldRestrictions OVERWORLD_DEEP_DARK =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomes(Biomes.DEEP_DARK.identifier())
                        .withMustBeCaughtBelowY(50);

        public static final WorldRestrictions OVERWORLD_RIVER =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_RIVER)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_OCEAN =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags((StarcatcherTags.IS_OCEAN))
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_WARM_OCEAN =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_WARM_OCEAN)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_DEEP_OCEAN =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_DEEP_OCEAN)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_LAKE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesBlacklistTags(StarcatcherTags.IS_OCEAN, StarcatcherTags.IS_RIVER)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_FRESHWATER =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesBlacklistTags(StarcatcherTags.IS_OCEAN)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_COLD_FRESHWATER =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_COLD_LAKE, StarcatcherTags.IS_COLD_RIVER)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_WARM_FRESHWATER =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_WARM_LAKE, StarcatcherTags.IS_WARM_RIVER)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_WARM_LAKE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_WARM_LAKE)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_COLD_RIVER =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_COLD_RIVER)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_COLD_OCEAN =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_COLD_OCEAN)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_COLD_LAKE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_COLD_LAKE)
                        .withMustBeCaughtAboveY(50)
                        .withMustBeCaughtBelowY(100);

        public static final WorldRestrictions OVERWORLD_COLD_MOUNTAIN =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_COLD_LAKE)
                        .withMustBeCaughtAboveY(100);

        public static final WorldRestrictions OVERWORLD_BEACH =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_BEACH)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_MUSHROOM_FIELDS =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_MUSHROOM_FIELDS)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_JUNGLE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(BiomeTags.IS_JUNGLE.location())
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_TAIGA =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(BiomeTags.IS_TAIGA.location())
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_CHERRY_GROVE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_CHERRY_GROVE)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_SWAMP =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_SWAMP)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_DARK_FOREST =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(StarcatcherTags.IS_DARK_FOREST)
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_FOREST =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withBiomesTags(BiomeTags.IS_FOREST.location())
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_SURFACE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_LAVA_SURFACE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"))
                        .withMustBeCaughtAboveY(50);

        public static final WorldRestrictions OVERWORLD_LAVA_UNDERGROUND =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"))
                        .withMustBeCaughtBelowY(50);

        public static final WorldRestrictions OVERWORLD_UNDERGROUND =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withMustBeCaughtBelowY(50);

        public static final WorldRestrictions OVERWORLD_LAVA_DEEPSLATE =
                WorldRestrictions.DEFAULT
                        .withDims(Level.OVERWORLD.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"))
                        .withMustBeCaughtBelowY(0);

        public static final WorldRestrictions NETHER_LAVA =
                WorldRestrictions.DEFAULT
                        .withDims(Level.NETHER.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"));

        public static final WorldRestrictions NETHER_LAVA_CRIMSON_FOREST =
                WorldRestrictions.DEFAULT
                        .withDims(Level.NETHER.identifier())
                        .withBiomes(Biomes.CRIMSON_FOREST.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"));

        public static final WorldRestrictions NETHER_LAVA_WARPED_FOREST =
                WorldRestrictions.DEFAULT
                        .withDims(Level.NETHER.identifier())
                        .withBiomes(Biomes.WARPED_FOREST.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"));

        public static final WorldRestrictions NETHER_LAVA_SOUL_SAND_VALLEY =
                WorldRestrictions.DEFAULT
                        .withDims(Level.NETHER.identifier())
                        .withBiomes(Biomes.SOUL_SAND_VALLEY.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"));

        public static final WorldRestrictions NETHER_LAVA_BASALT_DELTAS =
                WorldRestrictions.DEFAULT
                        .withDims(Level.NETHER.identifier())
                        .withBiomes(Biomes.BASALT_DELTAS.identifier())
                        .withFluids(Identifier.withDefaultNamespace("lava"));

        public static final WorldRestrictions END =
                WorldRestrictions.DEFAULT
                        .withDims(Level.END.identifier());

        public static final WorldRestrictions END_OUTER_ISLANDS =
                WorldRestrictions.DEFAULT
                        .withDims(Level.END.identifier())
                        .withBiomesTags(BiomeTags.IS_END.location())
                        .withBiomesBlacklist(Biomes.THE_END.identifier());

        public WorldRestrictions withDims(Identifier... dims)
        {
            return new WorldRestrictions(List.of(dims), this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withDimsBlacklist(Identifier... dimsBlacklist)
        {
            return new WorldRestrictions(this.dims, List.of(dimsBlacklist), this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withBiomes(Identifier... biome)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, List.of(biome), this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withBiomesTags(Identifier... biomesTag)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, List.of(biomesTag), this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withBiomesBlacklist(Identifier... biomesBlacklist)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, List.of(biomesBlacklist), this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withBiomesBlacklistTags(Identifier... biomesBlacklistTags)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, List.of(biomesBlacklistTags), this.fluids, this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withFluids(Identifier... fluids)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, List.of(fluids), this.seasons, this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withSeasons(Seasons... seasons)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, Arrays.stream(seasons).toList(), this.mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withMustBeCaughtBelowY(int mustBeCaughtBelowY)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, mustBeCaughtBelowY, this.mustBeCaughtAboveY);
        }

        public WorldRestrictions withMustBeCaughtAboveY(int mustBeCaughtAboveY)
        {
            return new WorldRestrictions(this.dims, this.dimsBlacklist, this.biomes, this.biomesTags, this.biomesBlacklist, this.biomesBlacklistTags, this.fluids, this.seasons, this.mustBeCaughtBelowY, mustBeCaughtAboveY);
        }

    }

    //region dif
    public record Difficulty(
            int speed,
            int penalty,
            float decay,
            List<Identifier> modifiers,
            List<SweetSpot> sweetSpots
    )
    {

        public Difficulty(int speed, int penalty, float decay, List<Identifier> modifiers, SweetSpot... sweetSpots)
        {
            this(speed, penalty, decay, modifiers, Arrays.stream(sweetSpots).toList());
        }

        public Difficulty withModifiers(List<Identifier> modifiers)
        {
            return new Difficulty(this.speed, this.penalty, this.decay, modifiers, this.sweetSpots);
        }

        public Difficulty vanishing(float vanishingRate)
        {
            List<SweetSpot> sss = new ArrayList<>();
            sweetSpots.forEach(s -> sss.add(s.vanishing(vanishingRate)));
            return new Difficulty(speed, penalty, decay, modifiers, sss);
        }

        public Difficulty vanishing()
        {
            List<SweetSpot> sss = new ArrayList<>();
            sweetSpots.forEach(s -> sss.add(s.vanishing(0.1f)));
            return new Difficulty(speed, penalty, decay, modifiers, sss);
        }

        public Difficulty moving(float movingRate)
        {
            List<SweetSpot> sss = new ArrayList<>();
            sweetSpots.forEach(s -> sss.add(s.moving(movingRate)));
            return new Difficulty(speed, penalty, decay, modifiers, sss);
        }

        public Difficulty moving()
        {
            List<SweetSpot> sss = new ArrayList<>();
            sweetSpots.forEach(s -> sss.add(s.moving(1)));
            return new Difficulty(speed, penalty, decay, modifiers, sss);
        }

        public Difficulty flip()
        {
            List<SweetSpot> sss = new ArrayList<>();
            sweetSpots.forEach(s -> sss.add(s.flip()));
            return new Difficulty(speed, penalty, decay, modifiers, sss);
        }


        //region preset difficulties

        public static Difficulty TRASH = new Difficulty(
                10, 0, 0,
                List.of(),
                SweetSpot.TRASH, SweetSpot.TRASH
        );

        public static Difficulty EASY = new Difficulty(
                9, 5, 1,
                List.of(),
                SweetSpot.NORMAL, SweetSpot.NORMAL
        );
        public static Difficulty EASY_VANISHING = EASY.vanishing();
        public static Difficulty EASY_MOVING = EASY.moving();

        public static Difficulty MEDIUM = new Difficulty(
                10, 20, 1,
                List.of(),
                SweetSpot.NORMAL, SweetSpot.THIN
        );
        public static Difficulty MEDIUM_VANISHING = MEDIUM.vanishing();
        public static Difficulty MEDIUM_MOVING = MEDIUM.moving();
        public static Difficulty MEDIUM_VANISHING_MOVING = MEDIUM.moving().vanishing();

        public static Difficulty HARD = new Difficulty(
                11, 10, 1,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN);
        public static Difficulty HARD_VANISHING = HARD.vanishing();
        public static Difficulty HARD_MOVING = HARD.moving();

        public static Difficulty THIN_NO_DECAY_NOT_FORGIVING = new Difficulty(
                11, 40, 0,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN
        );

        public static Difficulty HEAVY_FIVE_NORMAL = new Difficulty(
                5, 40, 0,
                List.of(),
                SweetSpot.NORMAL_HEAVY, SweetSpot.NORMAL_HEAVY, SweetSpot.NORMAL_HEAVY, SweetSpot.NORMAL_HEAVY, SweetSpot.NORMAL_HEAVY
        );

        public static Difficulty FOUR_BIG = new Difficulty(
                9, 20, 0,
                List.of(),
                SweetSpot.NORMAL, SweetSpot.NORMAL, SweetSpot.NORMAL, SweetSpot.NORMAL
        );
        public static Difficulty FOUR_BIG_VANISHING = FOUR_BIG.vanishing();
        public static Difficulty FOUR_BIG_MOVING = FOUR_BIG.moving();

        public static Difficulty HEAVY_EIGHT_AQUA = new Difficulty(
                12, 20, 0,
                List.of(),
                SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1
        );

        public static Difficulty HEAVY_EIGHT_AQUA_MOVING = new Difficulty(
                12, 20, 0,
                List.of(),
                SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1
        ).moving();

        public static Difficulty TWO_AQUA_ONE_THIN = new Difficulty(
                9, 20, 0,
                List.of(),
                SweetSpot.AQUA, SweetSpot.AQUA, SweetSpot.THIN
        );
        public static Difficulty TWO_AQUA_ONE_THIN_VANISHING = TWO_AQUA_ONE_THIN.vanishing();

        public static Difficulty FOUR_THIN = new Difficulty(
                9, 20, 0,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN
        );

        public static Difficulty FOUR_THIN_VANISHING = FOUR_THIN.vanishing();
        public static Difficulty FOUR_THIN_MOVING = FOUR_THIN.moving();

        public static Difficulty EIGHT_THIN = new Difficulty(
                9, 20, 0,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN
        );
        public static Difficulty EIGHT_THIN_VANISHING = EIGHT_THIN.vanishing();
        public static Difficulty EIGHT_THIN_MOVING = EIGHT_THIN.moving();
        public static Difficulty EIGHT_THIN_MOVING_VANISHING = EIGHT_THIN.vanishing();

        public static Difficulty THREE_BIG_TWO_THIN = new Difficulty(
                9, 20, 0,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN
        ).vanishing();
        public static Difficulty THREE_BIG_TWO_THIN_VANISHING = THREE_BIG_TWO_THIN.vanishing();

        public static Difficulty EIGHT_STONE_SPOTS = new Difficulty(
                12, 20, 0,
                List.of(),
                SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE
        );

        public static Difficulty FOUR_STONE_SPOTS = new Difficulty(
                12, 20, 0,
                List.of(),
                SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE, SweetSpot.STONE
        );

        public static Difficulty EASY_FAST_FISH = new Difficulty(
                15, 20, 1,
                List.of(),
                SweetSpot.NORMAL, SweetSpot.NORMAL
        );

        public static Difficulty SINGLE_AQUA = new Difficulty(
                13, 5, 1,
                List.of(),
                SweetSpot.AQUA
        );

        public static Difficulty SINGLE_AQUA_MOVING = new Difficulty(
                13, 5, 1,
                List.of(),
                SweetSpot.AQUA
        ).moving();

        public static Difficulty SINGLE_BIG_FAST = new Difficulty(
                13, 30, 1,
                List.of(),
                SweetSpot.NORMAL_STEADY
        );
        public static Difficulty SINGLE_BIG_FAST_MOVING = SINGLE_BIG_FAST.moving();
        public static Difficulty SINGLE_BIG_FAST_VANISHING = SINGLE_BIG_FAST.vanishing();

        public static Difficulty TWO_AQUA = new Difficulty(
                10, 20, 1,
                List.of(),
                SweetSpot.AQUA, SweetSpot.AQUA
        );

        public static Difficulty FOUR_AQUA = new Difficulty(
                10, 20, 1,
                List.of(),
                SweetSpot.AQUA, SweetSpot.AQUA
        );

        public static Difficulty SINGLE_THIN_FAST = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.THIN
        );

        public static Difficulty TWO_THIN = new Difficulty(
                11, 15, 1,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN
        );

        public static Difficulty TWO_THIN_NO_DECAY = new Difficulty(
                10, 20, 0,
                List.of(),
                SweetSpot.THIN, SweetSpot.THIN
        );

        public static Difficulty NON_STOP_ACTION_THREE_BIG = new Difficulty(
                14, 20, 1,
                List.of(),
                SweetSpot.NORMAL, SweetSpot.NORMAL, SweetSpot.NORMAL
        );

        public static Difficulty NON_STOP_ACTION_AQUA = new Difficulty(
                14, 2, 0.2f,
                List.of(),
                SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1, SweetSpot.AQUA_1
        );

        public static Difficulty WITHER = new Difficulty(
                10, 30, 1,
                List.of(),
                SweetSpot.WITHER_BIG, SweetSpot.WITHER, SweetSpot.WITHER
        );

        public static Difficulty CREEPER = new Difficulty(
                10, 20, 1,
                List.of(ModMinigameModifiers.SPAWN_TNT_SWEET_SPOTS.getFirst()),
                SweetSpot.CREEPER, SweetSpot.CREEPER
        );


        public static Difficulty DEEPSLATE_CRAB = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.DEEPSLATE_CRAB_CLAW, SweetSpot.DEEPSLATE_CRAB_CLAW,
                SweetSpot.DEEPSLATE_CRAB_LEG, SweetSpot.DEEPSLATE_CRAB_LEG, SweetSpot.DEEPSLATE_CRAB_LEG,
                SweetSpot.DEEPSLATE_CRAB_LEG, SweetSpot.DEEPSLATE_CRAB_LEG, SweetSpot.DEEPSLATE_CRAB_LEG);

        public static Difficulty OBSIDIAN_CRAB = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.OBSIDIAN_CRAB_CLAW, SweetSpot.OBSIDIAN_CRAB_CLAW,
                SweetSpot.OBSIDIAN_CRAB_LEG, SweetSpot.OBSIDIAN_CRAB_LEG, SweetSpot.OBSIDIAN_CRAB_LEG,
                SweetSpot.OBSIDIAN_CRAB_LEG, SweetSpot.OBSIDIAN_CRAB_LEG, SweetSpot.OBSIDIAN_CRAB_LEG);

        public static Difficulty NETHER_CRAB = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.NETHER_CRAB_CLAW, SweetSpot.NETHER_CRAB_CLAW,
                SweetSpot.NETHER_CRAB_LEG, SweetSpot.NETHER_CRAB_LEG, SweetSpot.NETHER_CRAB_LEG,
                SweetSpot.NETHER_CRAB_LEG, SweetSpot.NETHER_CRAB_LEG, SweetSpot.NETHER_CRAB_LEG);

        public static Difficulty END_CRAB = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.END_CRAB_CLAW, SweetSpot.END_CRAB_CLAW,
                SweetSpot.END_CRAB_LEG, SweetSpot.END_CRAB_LEG, SweetSpot.END_CRAB_LEG,
                SweetSpot.END_CRAB_LEG, SweetSpot.END_CRAB_LEG, SweetSpot.END_CRAB_LEG);

        public static Difficulty VOIDBITER = new Difficulty(
                14, 10, 1,
                List.of(),
                SweetSpot.AQUA, SweetSpot.THIN, SweetSpot.THIN);

        public static Difficulty JOEL = new Difficulty(
                14, 5, 1,
                List.of(),
                SweetSpot.AQUA_1, SweetSpot.AQUA_1
        );

        public static Difficulty CERBERAY = new Difficulty(
                16, 10, 1.5f,
                List.of(ModMinigameModifiers.SPAWN_TNT_SWEET_SPOTS_PLUS.getFirst(), ModMinigameModifiers.NIKDO53_MODIFIER.getFirst()),
                SweetSpot.THIN, SweetSpot.THIN, SweetSpot.THIN
        );

        //endregion preset difficulties


        public static final Codec<Difficulty> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("speed").forGetter(Difficulty::speed),
                        Codec.INT.fieldOf("missPenalty").forGetter(Difficulty::penalty),
                        Codec.FLOAT.fieldOf("decay").forGetter(Difficulty::decay),
                        Identifier.CODEC.listOf().fieldOf("modifiers").forGetter(Difficulty::modifiers),
                        SweetSpot.LIST_CODEC.fieldOf("sweetspots").forGetter(Difficulty::sweetSpots)
                ).apply(instance, Difficulty::new));


        public static final StreamCodec<FriendlyByteBuf, Difficulty> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, Difficulty::speed,
                ByteBufCodecs.INT, Difficulty::penalty,
                ByteBufCodecs.FLOAT, Difficulty::decay,
                Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), Difficulty::modifiers,
                SweetSpot.LIST_STREAM_CODEC, Difficulty::sweetSpots,
                Difficulty::new
        );
    }

    public record SweetSpot(
            Identifier sweetSpotType,
            Identifier texturePath,
            int size,
            int reward,
            boolean isFlip,
            float vanishingRate,
            float movingRate,
            int particleColor
    )
    {
        public SweetSpot(Identifier sweetSpotType, Identifier texturePath, int size, int reward, int particleColor)
        {
            this(sweetSpotType, texturePath, size, reward, false, 0, 0, particleColor);
        }

        private static final Identifier RL_NORMAL = Starcatcher.rl("textures/gui/minigame/spots/normal.png");
        private static final Identifier RL_NORMAL_STEADY = Starcatcher.rl("textures/gui/minigame/spots/normal_steady.png");
        private static final Identifier RL_THIN = Starcatcher.rl("textures/gui/minigame/spots/thin.png");
        private static final Identifier RL_THIN_STEADY = Starcatcher.rl("textures/gui/minigame/spots/thin_steady.png");
        private static final Identifier RL_FREEZE = Starcatcher.rl("textures/gui/minigame/spots/frozen.png");
        private static final Identifier RL_TREASURE = Starcatcher.rl("textures/gui/minigame/spots/treasure.png");
        private static final Identifier RL_WITHER = Starcatcher.rl("textures/gui/minigame/spots/wither.png");
        private static final Identifier RL_WITHER_BIG = Starcatcher.rl("textures/gui/minigame/spots/wither_big.png");
        private static final Identifier RL_CREEPER = Starcatcher.rl("textures/gui/minigame/spots/creeper.png");
        private static final Identifier RL_TNT = Starcatcher.rl("textures/gui/minigame/spots/tnt.png");
        private static final Identifier RL_STONE = Starcatcher.rl("textures/gui/minigame/spots/stone.png");
        private static final Identifier RL_AQUA = Starcatcher.rl("textures/gui/minigame/spots/aqua.png");

        private static final Identifier RL_NETHER_CRAB_CLAW = Starcatcher.rl("textures/gui/minigame/spots/nether_crab_claw.png");
        private static final Identifier RL_NETHER_CRAB_LEG = Starcatcher.rl("textures/gui/minigame/spots/nether_crab_leg.png");

        private static final Identifier RL_END_CRAB_LEG = Starcatcher.rl("textures/gui/minigame/spots/end_crab_leg.png");
        private static final Identifier RL_END_CRAB_CLAW = Starcatcher.rl("textures/gui/minigame/spots/end_crab_claw.png");

        private static final Identifier RL_DEEPSLATE_CRAB_LEG = Starcatcher.rl("textures/gui/minigame/spots/deepslate_crab_leg.png");
        private static final Identifier RL_DEEPSLATE_CRAB_CLAW = Starcatcher.rl("textures/gui/minigame/spots/deepslate_crab_claw.png");

        private static final Identifier RL_OBSIDIAN_CRAB_LEG = Starcatcher.rl("textures/gui/minigame/spots/obsidian_crab_leg.png");
        private static final Identifier RL_OBSIDIAN_CRAB_CLAW = Starcatcher.rl("textures/gui/minigame/spots/obsidian_crab_claw.png");

        private static final Identifier RL_THIN_STEADY_MOSSY = Starcatcher.rl("textures/gui/minigame/spots/thin_mossy.png");


        public SweetSpot flip()
        {
            return new SweetSpot(this.sweetSpotType, this.texturePath, this.size, this.reward, true, this.vanishingRate, this.movingRate, this.particleColor);
        }

        public SweetSpot vanishing(float vanishingRate)
        {
            return new SweetSpot(this.sweetSpotType, this.texturePath, this.size, this.reward, this.isFlip, vanishingRate, this.movingRate, this.particleColor);
        }

        public SweetSpot moving(float movingRate)
        {
            return new SweetSpot(this.sweetSpotType, this.texturePath, this.size, this.reward, this.isFlip, this.vanishingRate, movingRate, this.particleColor);
        }

        public static SweetSpot TRASH = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NORMAL,
                22,
                30,
                0x00ff00
        );

        public static SweetSpot NORMAL_STEADY = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NORMAL_STEADY,
                33,
                15,
                0x00ff00
        );

        public static SweetSpot NORMAL = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NORMAL,
                22,
                15,
                0x00ff00
        );

        public static SweetSpot THIN_STEADY = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_THIN_STEADY,
                20,
                20,
                0x00ff00
        );

        public static SweetSpot THIN_STEADY_MOSSY = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_THIN_STEADY_MOSSY,
                20,
                10,
                true, 0.01f, 1,
                0x00ff00
        );

        public static SweetSpot THIN = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_THIN,
                15,
                20,
                0x00ff00
        );

        public static SweetSpot FREEZE = new SweetSpot(
                ModSweetSpotsBehaviour.FROZEN,
                RL_FREEZE,
                33,
                15,
                0x095f92
        );

        public static SweetSpot NORMAL_HEAVY = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NORMAL,
                22,
                1,
                0x00ff00
        );

        public static SweetSpot TREASURE = new SweetSpot(
                ModSweetSpotsBehaviour.TREASURE,
                RL_TREASURE,
                20,
                15,
                0xFFD700
        );

        public static SweetSpot WITHER = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_WITHER,
                22,
                15,
                false,
                0,
                3,
                0x1f1f1f
        );

        public static SweetSpot WITHER_BIG = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_WITHER_BIG,
                33,
                15,
                0x1f1f1f
        );

        public static SweetSpot CREEPER = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_CREEPER,
                22,
                15,
                0x515353
        );

        public static SweetSpot TNT = new SweetSpot(
                ModSweetSpotsBehaviour.TNT,
                RL_TNT,
                33,
                30,
                0xff0000
        );

        public static SweetSpot STONE = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_STONE,
                33,
                1,
                0x494949
        );

        public static SweetSpot AQUA = new SweetSpot(
                ModSweetSpotsBehaviour.AQUA,
                RL_AQUA,
                22,
                15,
                0x387982
        );

        public static SweetSpot AQUA_1 = new SweetSpot(
                ModSweetSpotsBehaviour.AQUA,
                RL_AQUA, 22, 1, 0x387982
        );

        public static SweetSpot AQUA_5 = new SweetSpot(
                ModSweetSpotsBehaviour.AQUA,
                RL_AQUA, 22, 1, 0x387982
        );

        public static SweetSpot AQUA_10 = new SweetSpot(
                ModSweetSpotsBehaviour.AQUA,
                RL_AQUA, 22, 10, 0x387982
        );

        public static SweetSpot DEEPSLATE_CRAB_CLAW = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_DEEPSLATE_CRAB_CLAW, 24, 10, 0xff8400
        );

        public static SweetSpot DEEPSLATE_CRAB_LEG = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_DEEPSLATE_CRAB_LEG, 15, 1, 0xff8400
        );

        public static SweetSpot OBSIDIAN_CRAB_CLAW = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_OBSIDIAN_CRAB_CLAW, 24, 10, 0x3b2754
        );

        public static SweetSpot OBSIDIAN_CRAB_LEG = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_OBSIDIAN_CRAB_LEG, 15, 1, 0x3b2754
        );

        public static SweetSpot NETHER_CRAB_CLAW = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NETHER_CRAB_CLAW, 24, 10, 0xcd4545
        );

        public static SweetSpot NETHER_CRAB_LEG = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_NETHER_CRAB_LEG, 15, 1, 0xcd4545
        );

        public static SweetSpot END_CRAB_CLAW = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_END_CRAB_CLAW, 24, 10, 0xc67ed9
        );

        public static SweetSpot END_CRAB_LEG = new SweetSpot(
                ModSweetSpotsBehaviour.NORMAL,
                RL_END_CRAB_LEG, 15, 1, 0xc67ed9
        );


        public static final Codec<SweetSpot> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("sweet_spot_type").forGetter(SweetSpot::sweetSpotType),
                        Identifier.CODEC.fieldOf("texture_path").forGetter(SweetSpot::texturePath),
                        Codec.INT.fieldOf("hitbox_size_in_pixels").forGetter(SweetSpot::size),
                        Codec.INT.fieldOf("reward").forGetter(SweetSpot::reward),
                        Codec.BOOL.fieldOf("is_flip").forGetter(SweetSpot::isFlip),
                        Codec.FLOAT.fieldOf("vanishing_rate").forGetter(SweetSpot::vanishingRate),
                        Codec.FLOAT.fieldOf("moving_rate").forGetter(SweetSpot::movingRate),
                        Codec.INT.fieldOf("color_as_int").forGetter(SweetSpot::particleColor)
                ).apply(instance, SweetSpot::new));

        public static final Codec<List<SweetSpot>> LIST_CODEC = CODEC.listOf();

        public static final StreamCodec<FriendlyByteBuf, SweetSpot> STREAM_CODEC = ExtraComposites.composite(
                Identifier.STREAM_CODEC, SweetSpot::sweetSpotType,
                Identifier.STREAM_CODEC, SweetSpot::texturePath,
                ByteBufCodecs.INT, SweetSpot::size,
                ByteBufCodecs.INT, SweetSpot::reward,
                ByteBufCodecs.BOOL, SweetSpot::isFlip,
                ByteBufCodecs.FLOAT, SweetSpot::vanishingRate,
                ByteBufCodecs.FLOAT, SweetSpot::movingRate,
                ByteBufCodecs.INT, SweetSpot::particleColor,
                SweetSpot::new
        );

        public static final StreamCodec<FriendlyByteBuf, List<SweetSpot>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
    }

    //endregion dif


    public record SizeAndWeight(float sizeAverage, float sizeDeviation, float weightAverage, float weightDeviation)
    {
        public static final SizeAndWeight DEFAULT = new SizeAndWeight(41f, 21f, 2001f, 701f);
        public static final SizeAndWeight NONE = new SizeAndWeight(0, 0, 0, 0);

        public static final Codec<SizeAndWeight> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("average_size_cm").forGetter(SizeAndWeight::sizeAverage),
                        Codec.FLOAT.fieldOf("deviation_size_cm").forGetter(SizeAndWeight::sizeDeviation),
                        Codec.FLOAT.fieldOf("average_weight_grams").forGetter(SizeAndWeight::weightAverage),
                        Codec.FLOAT.fieldOf("deviation_weight_grams").forGetter(SizeAndWeight::weightDeviation)
                ).apply(instance, SizeAndWeight::new));

        public static final StreamCodec<ByteBuf, SizeAndWeight> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, SizeAndWeight::sizeAverage,
                ByteBufCodecs.FLOAT, SizeAndWeight::sizeDeviation,
                ByteBufCodecs.FLOAT, SizeAndWeight::weightAverage,
                ByteBufCodecs.FLOAT, SizeAndWeight::weightDeviation,
                SizeAndWeight::new
        );
    }


    public enum Rarity implements StringRepresentable
    {
        COMMON("common", 4, "", "", Style.EMPTY.applyFormat(ChatFormatting.WHITE), 40),
        UNCOMMON("uncommon", 8, "<gradient-37>", "</gradient-43>", Style.EMPTY.applyFormat(ChatFormatting.GREEN), 40),
        RARE("rare", 12, "<gradient-57>", "</gradient-63>", Style.EMPTY.applyFormat(ChatFormatting.BLUE), 30),
        EPIC("epic", 20, "<gradient-80>", "</gradient-90>", Style.EMPTY.applyFormat(ChatFormatting.LIGHT_PURPLE), 10),
        LEGENDARY("legendary", 35, "<rgb>", "</rgb>", Style.EMPTY.applyFormat(ChatFormatting.GOLD), 10);

        public static final Codec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);
        public static final StreamCodec<FriendlyByteBuf, Rarity> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Rarity.class);
        private final String key;
        private final int xp;
        private final String pre;
        private final String post;
        private final Style style;
        private final int stoneHookGraceTicks;

        Rarity(String key, int xp, String pre, String post, Style style, int stoneHookGraceTicks)
        {
            this.key = key;
            this.xp = xp;
            this.pre = pre;
            this.post = post;
            this.style = style;
            this.stoneHookGraceTicks = stoneHookGraceTicks;
        }

        public String getSerializedName()
        {
            return this.key;
        }

        public int getStoneHookGraceTicks()
        {
            return stoneHookGraceTicks;
        }

        public int getId()
        {
            return this.ordinal();
        }

        public int getXp()
        {
            return xp;
        }

        public String getPre()
        {
            return pre;
        }

        public String getPost()
        {
            return post;
        }

        public Style getStyle()
        {
            return style;
        }
    }

    public enum Daytime implements StringRepresentable
    {
        ALL("all"),
        DAY("day"),
        NOON("noon"),
        NIGHT("night"),
        MIDNIGHT("midnight");

        public static final Codec<Daytime> CODEC = StringRepresentable.fromEnum(Daytime::values);
        public static final StreamCodec<FriendlyByteBuf, Daytime> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Daytime.class);
        private final String key;

        Daytime(String key)
        {
            this.key = key;
        }

        public String getSerializedName()
        {
            return this.key;
        }
    }

    public enum Weather implements StringRepresentable
    {
        ALL("all"),
        CLEAR("clear"),
        RAIN("rain"),
        THUNDER("thunder");

        public static final Codec<Weather> CODEC = StringRepresentable.fromEnum(Weather::values);
        public static final StreamCodec<FriendlyByteBuf, Weather> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Weather.class);
        private final String key;

        Weather(String key)
        {
            this.key = key;
        }

        public String getSerializedName()
        {
            return this.key;
        }
    }

    public static List<Identifier> getBiomesAsList(FishProperties fp, Level level)
    {
        List<Identifier> rls = new ArrayList<>();

        for (Identifier rl : fp.wr.biomesTags)
        {
            TagKey<Biome> biomeBeingChecked = TagKey.create(Registries.BIOME, rl);

            Optional<HolderSet.Named<Biome>> optional = level.registryAccess().lookupOrThrow(Registries.BIOME).get(biomeBeingChecked);

            if (optional.isPresent())
            {
                for (Holder<Biome> biomeHolder : optional.get())
                {
                    String biomeString = biomeHolder.getRegisteredName();

                    rls.add(Identifier.parse(biomeString));
                }
            }
        }

        for (Identifier rl : fp.wr.biomes)
        {
            Optional<Holder.Reference<Biome>> optional = level.registryAccess().lookupOrThrow(Registries.BIOME).get(ResourceKey.create(Registries.BIOME, rl));
            if (optional.isPresent()) if (!rls.contains(rl)) rls.add(rl);
        }

        return rls;
    }

    public static List<Identifier> getBiomesBlacklistAsList(FishProperties fp, Level level)
    {
        List<Identifier> rls = new ArrayList<>();

        for (Identifier rl : fp.wr.biomesBlacklistTags)
        {
            TagKey<Biome> biomeBeingChecked = TagKey.create(Registries.BIOME, rl);

            Optional<HolderSet.Named<Biome>> optional = level.registryAccess().lookupOrThrow(Registries.BIOME).get(biomeBeingChecked);

            if (optional.isPresent())
            {
                for (Holder<Biome> biomeHolder : optional.get())
                {
                    String biomeString = biomeHolder.getRegisteredName();

                    rls.add(Identifier.parse(biomeString));
                }
            }
        }

        for (Identifier rl : fp.wr.biomesBlacklist)
        {
            Optional<Holder.Reference<Biome>> optional = level.registryAccess().lookupOrThrow(Registries.BIOME).get(ResourceKey.create(Registries.BIOME, rl));
            if (optional.isPresent()) if (!rls.contains(rl)) rls.add(rl);
        }

        return rls;
    }

    public static List<FishProperties> getFPs(Level level)
    {
        return getFPs(level.registryAccess());
    }

    public static List<FishProperties> getFPs(RegistryAccess registryAccess)
    {
        return registryAccess.lookupOrThrow(Starcatcher.FISH_REGISTRY).stream().toList();
    }

    public static int getChance(FishProperties fp, Entity entity, ItemStack rod)
    {
        Level level = entity.level();

        if (!isSeasonCorrect(entity, fp)) return 0;

        if (!isDimensionCorrect(entity, fp)) return 0;

        if (!isBiomeCorrect(entity, fp)) return 0;

        if (!isElevationCorrect(entity, fp)) return 0;

        if (!isDaytimeCorrect(entity, fp)) return 0;

        if (!isWeatherCorrect(entity, fp, rod)) return 0;

        //fluid check
        boolean fluid = fp.wr.fluids.contains(BuiltInRegistries.FLUID.getKey(getSource(level.getFluidState(entity.blockPosition()).getType())));
        boolean fluidAbove = fp.wr.fluids.contains(BuiltInRegistries.FLUID.getKey(getSource(level.getFluidState(entity.blockPosition().above()).getType())));
        boolean fluidBelow = fp.wr.fluids.contains(BuiltInRegistries.FLUID.getKey(getSource(level.getFluidState(entity.blockPosition().below()).getType())));

        if (!fluid && !fluidAbove && !fluidBelow && entity instanceof FishingBobEntity)
            return 0;

        //correct bait chance bonus
        ItemStack bait = ModDataComponents.has(rod, ModDataComponents.BAIT) ? ModDataComponents.get(rod, ModDataComponents.BAIT).stack().copy() : ItemStack.EMPTY;
        if (fp.br().correctBait().contains(BuiltInRegistries.ITEM.getKey(bait.getItem())))
        {
            return fp.baseChance() + fp.br().correctBaitChanceAdded();
        }

        return fp.baseChance();
    }

    public static List<FishProperties> getFpsWithGuideEntryForArea(Entity entity)
    {
        List<FishProperties> list = new ArrayList<>();

        for (FishProperties fp : entity.level().registryAccess().lookupOrThrow(Starcatcher.FISH_REGISTRY))
            if (isDimensionCorrect(entity, fp) && isBiomeCorrect(entity, fp) && isElevationCorrect(entity, fp) && fp.hasGuideEntry && fp.baseChance != 0) list.add(fp);

        return list;
    }

    public static boolean isWeatherCorrect(Entity entity, FishProperties fp, ItemStack rod)
    {
        Level level = entity.level();
        ItemStack bait = ModDataComponents.has(rod, ModDataComponents.BAIT) ? ModDataComponents.get(rod, ModDataComponents.BAIT).stack().copy() : ItemStack.EMPTY;

        if (!bait.is(ModItems.METEOROLOGICAL_BAIT))
        {
            //clear check
            if (fp.weather() == Weather.CLEAR && (level.getRainLevel(0) > 0.5 || level.getThunderLevel(0) > 0.5))
            {
                return false;
            }

            //rain check
            if (fp.weather() == Weather.RAIN && level.getRainLevel(0) < 0.5)
            {
                return false;
            }

            //thunder check
            if (fp.weather() == Weather.THUNDER && level.getThunderLevel(0) < 0.5)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isDaytimeCorrect(Entity entity, FishProperties fp)
    {
        Level level = entity.level();
        //time check
        if (fp.daytime() != Daytime.ALL)
        {
            long time = level.getDayTime() % 24000;

            switch (fp.daytime())
            {
                case Daytime.DAY:
                    if (time >= 12700 && time <= 23000) return false;
                    break;

                case Daytime.NOON:
                    if (time <= 3500 || time >= 8500) return false;
                    break;

                case Daytime.NIGHT:
                    if (time >= 23000 || time <= 12700) return false;
                    break;

                case Daytime.MIDNIGHT:
                    if (time <= 16500 || time >= 19500) return false;
                    break;
            }
        }
        return true;
    }

    public static boolean isElevationCorrect(Entity entity, FishProperties fp)
    {
        //y level check >
        if (entity.position().y > fp.wr.mustBeCaughtBelowY()) return false;

        //y level check <
        if (entity.position().y < fp.wr.mustBeCaughtAboveY()) return false;

        return true;
    }

    public static boolean isBiomeCorrect(Entity entity, FishProperties fp)
    {
        Level level = entity.level();
        List<Identifier> biomes = getBiomesAsList(fp, level);
        List<Identifier> blacklist = getBiomesBlacklistAsList(fp, level);
        Identifier currentBiome = level.getBiome(entity.blockPosition()).getKey().identifier();

        if (!biomes.isEmpty() && !biomes.contains(currentBiome))
            return false;

        if (!blacklist.isEmpty() && blacklist.contains(currentBiome))
            return false;
        return true;
    }

    public static boolean isDimensionCorrect(Entity entity, FishProperties fp)
    {
        //dimension  check
        if (!fp.wr.dims.isEmpty() && !fp.wr().dims().contains(entity.level().dimension().identifier()))
            return false;

        if (fp.wr.dimsBlacklist.contains(entity.level().dimension().identifier()))
            return false;
        return true;
    }


    public static boolean isSeasonCorrect(Entity entity, FishProperties fp)
    {
        //Serene Seasons check
        if (ModList.get().isLoaded("sereneseasons") && Config.ENABLE_SEASONS.get())
        {
            if (!SereneSeasonsCompat.canCatch(fp, entity.level())) return false;
        }

        //Ecliptic Seasons check
        if (ModList.get().isLoaded("eclipticseasons") && Config.ENABLE_SEASONS.get())
        {
            if (!EclipticSeasonsCompat.canCatch(fp, entity.level())) return false;
        }

        //TerraFirmaCraft Seasons check
        if (ModList.get().isLoaded("tfc") && Config.ENABLE_SEASONS.get())
        {
            if (!TerraFirmaCraftSeasonsCompat.canCatch(fp, entity.level())) return false;
        }
        return true;
    }

    public static Fluid getSource(Fluid fluid1)
    {
        if (fluid1 instanceof FlowingFluid fluid)
        {
            return fluid.getSource();
        }

        return fluid1;
    }

    public static SizeAndWeight sizeWeight(float sizeAvg, float sizeDev, float weightAvg, float weightDev)
    {
        return new SizeAndWeight(sizeAvg, sizeDev, weightAvg, weightDev);
    }

    public static Identifier rl(String ns, String path)
    {
        return Identifier.fromNamespaceAndPath(ns, path);
    }

}
