package com.wdiscute.starcatcher.registry.fishing;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.registry.blocks.ModBlocks;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.TrophyProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;

import static com.wdiscute.starcatcher.registry.fishing.FishingPropertiesRegistry.*;

public class DGTrophies
{

    private static ResourceKey<TrophyProperties> createKey(TrophyProperties tp)
    {
        return ResourceKey.create(Starcatcher.TROPHY_REGISTRY, tp.fish().getKey().identifier());
    }

    private static void register(BootstrapContext<TrophyProperties> context, TrophyProperties.Builder builder)
    {
        TrophyProperties entry = builder.build();
        context.register(createKey(entry), entry);
    }

    @SuppressWarnings("deprecation")
    public static void bootstrap(BootstrapContext<TrophyProperties> context)
    {

        register(
                context, TrophyProperties.builder()
                        .setFish(ModBlocks.TROPHY_BRONZE.asItem().builtInRegistryHolder())
                        .hideUntilCaught()
                        .setTrophyType(TrophyProperties.TrophyType.TROPHY)
                        .setAllProgress(new TrophyProperties.RarityProgress(50, 20))
        );

        register(
                context, TrophyProperties.builder()
                        .setFish(ModBlocks.TROPHY_SILVER.asItem().builtInRegistryHolder())
                        .hideUntilCaught()
                        .setTrophyType(TrophyProperties.TrophyType.TROPHY)
                        .setAllProgress(new TrophyProperties.RarityProgress(100, 50))
        );

        register(
                context, TrophyProperties.builder()
                        .setFish(ModBlocks.TROPHY_GOLD.asItem().builtInRegistryHolder())
                        .hideUntilCaught()
                        .setTrophyType(TrophyProperties.TrophyType.TROPHY)
                        .setAllProgress(new TrophyProperties.RarityProgress(200, 0))
                        .withProgress(FishProperties.Rarity.COMMON, new TrophyProperties.RarityProgress(0, 36))
                        .withProgress(FishProperties.Rarity.UNCOMMON, new TrophyProperties.RarityProgress(0, 23))
                        .withProgress(FishProperties.Rarity.RARE, new TrophyProperties.RarityProgress(0, 14))
                        .withProgress(FishProperties.Rarity.EPIC, new TrophyProperties.RarityProgress(0, 13))
                        .withProgress(FishProperties.Rarity.LEGENDARY, new TrophyProperties.RarityProgress(0, 8))
        );
        //                                         ,--.
        // ,---.   ,---.   ,---. ,--.--.  ,---.  ,-'  '-.  ,---.
        //(  .-'  | .-. : | .--' |  .--' | .-. : '-.  .-' (  .-'
        //.-'  `) \   --. \ `--. |  |    \   --.   |  |   .-'  `)
        //`----'   `----'  `---' `--'     `----'   `--'   `----'
        //

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldFish(ModItems.DRIFTING_WATERLOGGED_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .setAllProgress(new TrophyProperties.RarityProgress(6, 15))
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldSurfaceLava(ModItems.SCALDING_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .setAllProgress(new TrophyProperties.RarityProgress(0, 27))
                        .setChanceToCatch(33)
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldSurfaceLava(ModItems.BURNING_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .setAllProgress(new TrophyProperties.RarityProgress(0, 42))
                        .setChanceToCatch(33)
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldDeepOceanFish(ModItems.HOPEFUL_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .withProgress(FishProperties.Rarity.EPIC, new TrophyProperties.RarityProgress(5, 0))
                        .setChanceToCatch(33)
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldDeepOceanFish(ModItems.HOPELESS_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .withProgress(FishProperties.Rarity.EPIC, new TrophyProperties.RarityProgress(5, 0))
                        .setChanceToCatch(33)
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(overworldRiverFish(ModItems.TRUE_BLUE_BOTTLE))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
                        .withProgress(FishProperties.Rarity.LEGENDARY, new TrophyProperties.RarityProgress(1, 0))
                        .setChanceToCatch(1)
        );

        register(
                context, TrophyProperties.builder()
                        .setFishProperties(
                                fish(ModItems.WITHERED_BOTTLE)
                                        .withBaseChance(0)
                                        .withBaitRestrictions(
                                                FishProperties.BaitRestrictions.DEFAULT
                                                        .withCorrectBait(BuiltInRegistries.ITEM.getKey(Items.WITHER_SKELETON_SKULL))
                                                        .withCorrectBaitChanceAdded(200)
                                        ))
                        .setTrophyType(TrophyProperties.TrophyType.SECRET)
        );

        //
        //          ,--.   ,--.
        // ,---.  ,-'  '-. |  ,---.   ,---.  ,--.--.  ,---.
        //| .-. | '-.  .-' |  .-.  | | .-. : |  .--' (  .-'
        //' '-' '   |  |   |  | |  | \   --. |  |    .-'  `)
        // `---'    `--'   `--' `--'  `----' `--'    `----'
        //

        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldDeepslateFish(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND)))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .withProgress(FishProperties.Rarity.RARE, new TrophyProperties.RarityProgress(1, 4))
        );

        register(
                context, TrophyProperties.builder().setFishProperties(
                                netherLavaFish(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLD_BLOCK)))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .withProgress(FishProperties.Rarity.LEGENDARY, new TrophyProperties.RarityProgress(3, 0))
                        .setChanceToCatch(33)
        );

        register(
                context, TrophyProperties.builder().setFishProperties(
                                netherLavaFish(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_SCRAP)))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .withProgress(FishProperties.Rarity.LEGENDARY, new TrophyProperties.RarityProgress(0, 10))
                        .setChanceToCatch(5)
                        .setRepeatable(true)
        );

        register(
                context, TrophyProperties.builder().setFishProperties(
                                netherLavaFish(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );


        //naturalist
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldForestFish(ModItems.NATURALIST_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //iceborn rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldColdOceanFish(ModItems.ICEBORN_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //magma forged
        register(
                context, TrophyProperties.builder().setFishProperties(
                                netherLavaBasaltDeltasFish(ModItems.MAGMAFORGED_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //bamboo rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldFish(ModItems.BAMBOO_ROD)
                                        .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD
                                                .withBiomes(Biomes.BAMBOO_JUNGLE.identifier())))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //bamboo rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldSurfaceLava(ModItems.OBSIDIAN_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //boner rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                netherLavaSoulSandValleyFish(ModItems.BONER_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //sky rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldFish(ModItems.SKY_ROD)
                                        .withWorldRestrictions(FishProperties.WorldRestrictions.OVERWORLD
                                                .withMustBeCaughtAboveY(300)))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //lush glowberry
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldLushCavesFish(ModItems.LUSH_GLOWBERRY_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

        //humble rod
        register(
                context, TrophyProperties.builder().setFishProperties(
                                overworldRiverFish(ModItems.HUMBLE_ROD))
                        .setTrophyType(TrophyProperties.TrophyType.EXTRA)
                        .setChanceToCatch(1)
                        .setRepeatable(true)
        );

    }
}
