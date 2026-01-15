package com.wdiscute.starcatcher.registry.fishing;

import com.mojang.datafixers.util.Pair;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.registry.fishing.compat.*;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FishingPropertiesRegistry
{

    public static void register()
    {
        DGMinecraftFishes.bootstrap();
        DGStarcatcherFishes.bootstrap();
        DGTideFishes.bootstrap();
        DGAquacultureFishes.bootstrap();
        DGFishOfThievesFishes.bootstrap();
        DGNetherDepthsUpgradeFishes.bootstrap();
        DGSullysModFishes.bootstrap();
        DGUpgradeAquaticFishes.bootstrap();
        DGEnvironmentalFishes.bootstrap();
        DGCollectorsReapFishes.bootstrap();
        DGMinersDelightFishes.bootstrap();
        DGAlexsCavesFishes.bootstrap();
        DGCrittersAndCompanionsFishes.bootstrap();
        DGHybridAquaticFishes.bootstrap();
        DGAquamiraeFishes.bootstrap();
        DGTerraFirmaCraftFishes.bootstrap();
    }

    //region builders
    protected static FishProperties.Builder fish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish);
    }

    protected static FishProperties.Builder overworldFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD);
    }

    protected static FishProperties.Builder endFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.END);
    }

    protected static FishProperties.Builder endOuterIslandsFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.END_OUTER_ISLANDS);
    }

    protected static FishProperties.Builder netherLavaFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.NETHER_LAVA);
    }

    protected static FishProperties.Builder netherLavaCrimsonForestFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.NETHER_LAVA_CRIMSON_FOREST);
    }

    protected static FishProperties.Builder netherLavaWarpedForestFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.NETHER_LAVA_WARPED_FOREST);
    }

    protected static FishProperties.Builder netherLavaSoulSandValleyFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.NETHER_LAVA_SOUL_SAND_VALLEY);
    }

    protected static FishProperties.Builder netherLavaBasaltDeltasFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.NETHER_LAVA_BASALT_DELTAS);
    }

    protected static FishProperties.Builder overworldLushCavesFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LUSH_CAVES)
                .withBaitRestrictions(FishProperties.BaitRestrictions.LUSH_BAIT);
    }

    protected static FishProperties.Builder overworldDeepDarkFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_DEEP_DARK)
                .withBaitRestrictions(FishProperties.BaitRestrictions.SCULK_BAIT);
    }

    protected static FishProperties.Builder overworldSurfaceFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_SURFACE);
    }

    protected static FishProperties.Builder overworldSurfaceLava(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LAVA_SURFACE);
    }

    protected static FishProperties.Builder overworldCavesFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_STONE_CAVES);
    }

    protected static FishProperties.Builder overworldDripstoneCavesFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_DRIPSTONE_CAVES)
                .withBaitRestrictions(FishProperties.BaitRestrictions.DRIPSTONE_BAIT);
    }

    protected static FishProperties.Builder overworldUndergroundFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_UNDERGROUND);
    }

    protected static FishProperties.Builder overworldUndergroundLava(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LAVA_UNDERGROUND);
    }

    protected static FishProperties.Builder overworldMountainFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LAKE
                        .withMustBeCaughtAboveY(100)
                        .withMustBeCaughtBelowY(Integer.MAX_VALUE));
    }

    protected static FishProperties.Builder overworldDeepslateFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_DEEPSLATE);
    }

    protected static FishProperties.Builder overworldDeepslateLava(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LAVA_DEEPSLATE);
    }

    protected static FishProperties.Builder overworldColdLakeFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_COLD_LAKE);
    }

    protected static FishProperties.Builder overworldWarmLakeFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_WARM_LAKE);
    }

    protected static FishProperties.Builder overworldWarmMountainFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_WARM_LAKE);
    }

    protected static FishProperties.Builder overworldColdMountainFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_COLD_MOUNTAIN);
    }

    protected static FishProperties.Builder overworldColdOceanFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_COLD_OCEAN);
    }

    protected static FishProperties.Builder overworldColdRiverFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_COLD_RIVER);
    }

    protected static FishProperties.Builder overworldLakeFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_LAKE);
    }

    protected static FishProperties.Builder overworldOceanFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_OCEAN);
    }

    protected static FishProperties.Builder overworldWarmOceanFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_WARM_OCEAN);
    }

    protected static FishProperties.Builder overworldDeepOceanFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_DEEP_OCEAN);
    }

    protected static FishProperties.Builder overworldRiverFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_RIVER);
    }

    protected static FishProperties.Builder overworldBeachFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_BEACH);
    }


    protected static FishProperties.Builder overworldMushroomFieldsFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_MUSHROOM_FIELDS);
    }

    protected static FishProperties.Builder overworldJungleFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_JUNGLE);
    }

    protected static FishProperties.Builder overworldTaigaFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_TAIGA);
    }

    protected static FishProperties.Builder overworldCherryGroveFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_CHERRY_GROVE)
                .withBaitRestrictions(FishProperties.BaitRestrictions.CHERRY_BAIT);
    }

    protected static FishProperties.Builder overworldSwampFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_SWAMP)
                .withBaitRestrictions(FishProperties.BaitRestrictions.MURKWATER_BAIT);
    }

    protected static FishProperties.Builder overworldDarkForestFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_DARK_FOREST);
    }

    protected static FishProperties.Builder overworldForestFish(Holder<Item> fish)
    {
        return FishProperties.builder().withFish(fish)
                .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD_FOREST);
    }

    //endregion

    private static final List<Pair<ResourceKey<FishProperties>, FishProperties>> PROPERTIES = new ArrayList<>();
    private static final List<ResourceKey<FishProperties>> COMPAT_KEYS = new ArrayList<>();

    static ResourceKey<FishProperties> createKey(FishProperties fp)
    {
        return ResourceKey.create(
                Starcatcher.FISH_REGISTRY, Identifier.parse(fp.catchInfo().fish()
                        .getRegisteredName()));
    }

    protected static void registerStarcatcherBucketAndEntity(FishProperties.Builder builder)
    {
        builder.withBucketedFish(ModItems.STARCAUGHT_BUCKET);
        builder.withEntityToSpawn(U.holderEntity("starcatcher", "fish"));
        builder.build();
        register(builder);
    }

    protected static void registerStarcatcherOnlyEntity(FishProperties.Builder builder)
    {
        builder.withEntityToSpawn(U.holderEntity("starcatcher", "fish"));
        builder.build();
        register(builder);
    }


    protected static void register(FishProperties.Builder builder)
    {
        FishProperties properties = builder.build();
        ResourceKey<FishProperties> key = FishingPropertiesRegistry.createKey(properties);
        PROPERTIES.add(Pair.of(key, properties));
        String namespace = key.identifier().getNamespace();
        if (!namespace.equals("minecraft") && !namespace.equals("starcatcher"))
            COMPAT_KEYS.add(key);
    }

    public static void registerConditions(BiConsumer<ResourceKey<?>, ICondition> consumer)
    {
        for (ResourceKey<FishProperties> compatKey : COMPAT_KEYS)
        {
            //fix for hybrid aquatic as their modid is hybrid_aquatic but items use hybrid-aquatic
            if(compatKey.identifier().getNamespace().equals("hybrid-aquatic"))
            {
                consumer.accept(compatKey, new ModLoadedCondition("hybrid_aquatic"));
                continue;
            }
            consumer.accept(compatKey, new ModLoadedCondition(compatKey.identifier().getNamespace()));
        }
    }

    public static void bootstrap(BootstrapContext<FishProperties> context)
    {
        PROPERTIES.forEach(p -> context.register(p.getFirst(), p.getSecond()));
    }
}
