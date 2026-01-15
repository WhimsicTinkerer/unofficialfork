package com.wdiscute.starcatcher.registry;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.guide.FishingGuideItem;
import com.wdiscute.starcatcher.items.*;
import com.wdiscute.starcatcher.items.cheater.*;
import com.wdiscute.starcatcher.items.helper.BasicItem;
import com.wdiscute.starcatcher.items.helper.FireResistantBasicItem;
import com.wdiscute.starcatcher.items.helper.SingleStackBasicItem;
import com.wdiscute.starcatcher.items.modifieritem.CatchModifierItem;
import com.wdiscute.starcatcher.items.modifieritem.MinigameModifierItem;
import com.wdiscute.starcatcher.items.modifieritem.TackleSkinItem;
import com.wdiscute.starcatcher.registry.custom.catchmodifiers.ModCatchModifiers;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.ModMinigameModifiers;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ModTackleSkins;
import com.wdiscute.starcatcher.rod.StarcatcherFishingRodItem;
import com.wdiscute.starcatcher.secretnotes.NoteContainer;
import com.wdiscute.starcatcher.secretnotes.SecretNote;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ModItems
{

    static void registerExtra()
    {
        //this works!
        if (ModList.get().isLoaded("tide"))
        {
            //DeferredItem<Item> FISH = ITEMS_REGISTRY.register("fish", FishItem::new);
        }
    }


    DeferredRegister.Items ITEMS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items RODS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items BAITS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items HOOKS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items BOBBERS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);

    //fishes which have a model and swim in water
    DeferredRegister.Items FISH_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items KINDA_BUT_NOT_REALLY_FISH_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items TRASH_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);

    DeferredRegister.Items TEMPLATES_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);
    DeferredRegister.Items BLOCKITEMS_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);

    DeferredRegister.Items DEV_REGISTRY = DeferredRegister.createItems(Starcatcher.MOD_ID);

    DeferredItem<Item> SETTINGS = DEV_REGISTRY.registerItem(
            "settings", Item::new);

    DeferredItem<Item> MISSINGNO = DEV_REGISTRY.registerItem("missingno", BasicItem::new);
    DeferredItem<Item> UNKNOWN_FISH = DEV_REGISTRY.registerItem("unknown_fish", BasicItem::new);

    DeferredItem<Item> GUIDE = ITEMS_REGISTRY.registerItem("starcatcher_guide", FishingGuideItem::new);

    DeferredItem<Item> FISH_RADAR = ITEMS_REGISTRY.registerItem("fish_radar", SingleStackBasicItem::new);

    DeferredItem<Item> STARCATCHER_TWINE = ITEMS_REGISTRY.registerItem("starcatcher_twine", SingleStackBasicItem::new);

    //hooks
    DeferredItem<Item> HOOK = HOOKS_REGISTRY.registerItem("hook", SingleStackBasicItem::new);
    DeferredItem<Item> SHINY_HOOK = HOOKS_REGISTRY.registerItem("shiny_hook", p -> new MinigameModifierItem(p, ModMinigameModifiers.SPAWN_TREASURE_ON_THREE_HITS));
    DeferredItem<Item> GOLD_HOOK = HOOKS_REGISTRY.registerItem("gold_hook", p -> new CatchModifierItem(p, ModCatchModifiers.EXTRA_EXP_BASED_ON_PERFORMANCE));
    DeferredItem<Item> MOSSY_HOOK = HOOKS_REGISTRY.registerItem("mossy_hook", p -> new MinigameModifierItem(p, ModMinigameModifiers.HARDER_WITH_TREASURE_ON_PERFECT));
    DeferredItem<Item> STONE_HOOK = HOOKS_REGISTRY.registerItem("stone_hook", p -> new MinigameModifierItem(p, ModMinigameModifiers.STOP_DECAY_ON_HIT));
    DeferredItem<Item> SPLIT_HOOK = HOOKS_REGISTRY.registerItem("split_hook", p -> new CatchModifierItem(p, ModCatchModifiers.EXTRA_ITEM));
    //TODO add stabilizing hook, no idea what for
    //DeferredItem<Item> STABILIZING_HOOK = HOOKS_REGISTRY.registerItem("stabilizing_hook", p -> new MinigameModifierItem(p, ModMinigameModifiers.NO_FLIP));
    DeferredItem<Item> HEAVY_HOOK = HOOKS_REGISTRY.registerItem("heavy_hook", p -> new MinigameModifierItem(p, ModMinigameModifiers.SLOWER_MOVING_SWEET_SPOTS));

    //bobbers
    DeferredItem<Item> BOBBER = BOBBERS_REGISTRY.registerItem("bobber", SingleStackBasicItem::new);
    DeferredItem<Item> STEADY_BOBBER = BOBBERS_REGISTRY.registerItem("steady_bobber", p -> new MinigameModifierItem(p, ModMinigameModifiers.BIGGER_GREEN_SWEET_SPOTS));
    DeferredItem<Item> CLEAR_BOBBER = BOBBERS_REGISTRY.registerItem("clear_bobber", p -> new MinigameModifierItem(p, ModMinigameModifiers.SLOWER_VANISHING));
    DeferredItem<Item> AQUA_BOBBER = BOBBERS_REGISTRY.registerItem("aqua_bobber", p -> new MinigameModifierItem(p, ModMinigameModifiers.ADD_AQUA_SWEET_SPOT));
    DeferredItem<Item> VANILLA_BOBBER = BOBBERS_REGISTRY.registerItem("vanilla_bobber", p -> new CatchModifierItem(p, ModCatchModifiers.VANILLA_LOOT));

    //baits
    DeferredItem<Item> WORM = BAITS_REGISTRY.registerItem("worm", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> ALMIGHTY_WORM = BAITS_REGISTRY.registerItem("almighty_worm", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME, ModCatchModifiers.FISH_ENTITY));
    DeferredItem<Item> SEEKING_WORM = BAITS_REGISTRY.registerItem("seeking_worm", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME, ModCatchModifiers.GUARANTEE_NEW_FISH_ALWAYS));

    DeferredItem<Item> GUNPOWDER_BAIT = BAITS_REGISTRY.registerItem("gunpowder_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> CHERRY_BAIT = BAITS_REGISTRY.registerItem("cherry_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> LUSH_BAIT = BAITS_REGISTRY.registerItem("lush_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> SCULK_BAIT = BAITS_REGISTRY.registerItem("sculk_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> DRIPSTONE_BAIT = BAITS_REGISTRY.registerItem("dripstone_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> MURKWATER_BAIT = BAITS_REGISTRY.registerItem("murkwater_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> LEGENDARY_BAIT = BAITS_REGISTRY.registerItem("legendary_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME));
    DeferredItem<Item> METEOROLOGICAL_BAIT = BAITS_REGISTRY.registerItem("meteorological_bait", p -> new CatchModifierItem(p, 64, ModCatchModifiers.DECREASES_LURE_TIME, ModCatchModifiers.IGNORE_DAYTIME_AND_WEATHER_RESTRICTIONS));


    //bobber skin templates
    DeferredItem<Item> PEARL_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("pearl_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.PEARL_TACKLE_SKIN));
    DeferredItem<Item> KIMBE_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("kimbe_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.KIMBE_TACKLE_SKIN));
    DeferredItem<Item> COLORFUL_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("colorful_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.COLORFUL_TACKLE_SKIN));
    DeferredItem<Item> CLEAR_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("clear_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.CLEAR_TACKLE_SKIN));
    DeferredItem<Item> FROG_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("frog_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.FROG_TACKLE_SKIN));
    DeferredItem<Item> KING_SMITHING_TEMPLATE = TEMPLATES_REGISTRY.registerItem("king_smithing_template", p -> new TackleSkinItem(p, ModTackleSkins.KING_TACKLE_SKIN));

    //rods
    DeferredItem<Item> ROD = RODS_REGISTRY.registerItem("starcatcher_rod", StarcatcherFishingRodItem::new);

    //fishing rod skins
    DeferredItem<Item> NATURALIST_ROD = RODS_REGISTRY.registerItem("naturalist_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> ICEBORN_ROD = RODS_REGISTRY.registerItem("iceborn_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> MAGMAFORGED_ROD = RODS_REGISTRY.registerItem("magmaforged_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> SLIMED_ROD = RODS_REGISTRY.registerItem("slimed_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> SHARKTOOTH_ROD = RODS_REGISTRY.registerItem("sharktooth_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> AZURE_CRYSTAL_ROD = RODS_REGISTRY.registerItem("azure_crystal_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> GOOD_OLD_ROD = RODS_REGISTRY.registerItem("good_old_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> BAMBOO_ROD = RODS_REGISTRY.registerItem("bamboo_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> OBSIDIAN_ROD = RODS_REGISTRY.registerItem("obsidian_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> ALPHA_ROD = RODS_REGISTRY.registerItem("alpha_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> BONER_ROD = RODS_REGISTRY.registerItem("boner_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> SKY_ROD = RODS_REGISTRY.registerItem("sky_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> LUSH_GLOWBERRY_ROD = RODS_REGISTRY.registerItem("lush_glowberry_rod", StarcatcherFishingRodItem::new);
    DeferredItem<Item> HUMBLE_ROD = RODS_REGISTRY.registerItem("humble_rod", StarcatcherFishingRodItem::new);

    //secrets
    DeferredItem<Item> SECRET_NOTE = ITEMS_REGISTRY.registerItem("secret_note", SecretNote::new);
    DeferredItem<Item> BROKEN_BOTTLE = ITEMS_REGISTRY.registerItem("broken_bottle", BrokenBottle::new);

    //notes
    DeferredItem<Item> DRIFTING_WATERLOGGED_BOTTLE = ITEMS_REGISTRY.registerItem("drifting_waterlogged_bottle", p -> new NoteContainer(p, SecretNote.Note.CRYSTAL_HOOK));

    DeferredItem<Item> SCALDING_BOTTLE = ITEMS_REGISTRY.registerItem("scalding_bottle", p -> new NoteContainer(p.stacksTo(1).fireResistant(), SecretNote.Note.ARNWULF_1, true));

    DeferredItem<Item> BURNING_BOTTLE = ITEMS_REGISTRY.registerItem("burning_bottle", p -> new NoteContainer(p.stacksTo(1).fireResistant(), SecretNote.Note.ARNWULF_2, true));

    DeferredItem<Item> HOPEFUL_BOTTLE = ITEMS_REGISTRY.registerItem("hopeful_bottle", p -> new NoteContainer(p, SecretNote.Note.HOPEFUL_NOTE));

    DeferredItem<Item> HOPELESS_BOTTLE = ITEMS_REGISTRY.registerItem("hopeless_bottle", p -> new NoteContainer(p, SecretNote.Note.HOPELESS_NOTE));

    DeferredItem<Item> TRUE_BLUE_BOTTLE = ITEMS_REGISTRY.registerItem("true_blue_bottle", p -> new NoteContainer(p, SecretNote.Note.TRUE_BLUE));

    DeferredItem<Item> WITHERED_BOTTLE = ITEMS_REGISTRY.registerItem("withered_bottle", p -> new NoteContainer(p, SecretNote.Note.WITHER));


    //cheater items
    DeferredItem<Item> AWARD_ALL_FISHES = DEV_REGISTRY.registerItem("award_all_fishes", AwardAllFishes::new);
    DeferredItem<Item> AWARD_ONE_FISH = DEV_REGISTRY.registerItem("award_one_fish", AwardOneFish::new);
    DeferredItem<Item> REVOKE_ALL_FISHES = DEV_REGISTRY.registerItem("revoke_all_fishes", RevokeAllFishes::new);

    DeferredItem<Item> AWARD_ALL_TROPHIES = DEV_REGISTRY.registerItem("award_all_trophies", AwardAllTrophies::new);
    DeferredItem<Item> REVOKE_ALL_TROPHIES = DEV_REGISTRY.registerItem("revoke_all_trophies", RevokeAllTrophies::new);

    DeferredItem<Item> AWARD_ALL_SECRETS = DEV_REGISTRY.registerItem("award_all_secrets", AwardAllSecrets::new);
    DeferredItem<Item> REVOKE_ALL_SECRETS = DEV_REGISTRY.registerItem("revoke_all_secrets", RevokeAllSecrets::new);

    DeferredItem<Item> REVOKE_ALL_EXTRAS = DEV_REGISTRY.registerItem("revoke_all_extras", RevokeAllExtras::new);

    //treasure
    DeferredItem<Item> WATERLOGGED_SATCHEL = ITEMS_REGISTRY.registerItem("waterlogged_satchel", WaterloggedSatchel::new);

    DeferredItem<Item> FISH_BONES = ITEMS_REGISTRY.registerItem("fish_bones", BasicItem::new);

    //
    //  ,---. ,--.         ,--.
    // /  .-' `--'  ,---.  |  ,---.   ,---.   ,---.
    // |  `-, ,--. (  .-'  |  .-.  | | .-. : (  .-'
    // |  .-' |  | .-'  `) |  | |  | \   --. .-'  `)
    // `--'   `--' `----'  `--' `--'  `----' `----'
    //

    //lake
    DeferredItem<Item> OBIDONTIEE = FISH_REGISTRY.registerItem("obidontiee", FishItem::new);
    DeferredItem<Item> SILVERVEIL_PERCH = FISH_REGISTRY.registerItem("silverveil_perch", FishItem::new);
    DeferredItem<Item> ELDERSCALE = FISH_REGISTRY.registerItem("elderscale", FishItem::new);
    DeferredItem<Item> DRIFTFIN = FISH_REGISTRY.registerItem("driftfin", FishItem::new);
    DeferredItem<Item> TWILIGHT_KOI = FISH_REGISTRY.registerItem("twilight_koi", FishItem::new);
    DeferredItem<Item> THUNDER_BASS = FISH_REGISTRY.registerItem("thunder_bass", FishItem::new);
    DeferredItem<Item> LIGHTNING_BASS = FISH_REGISTRY.registerItem("lightning_bass", FishItem::new);
    DeferredItem<Item> BOOT = TRASH_REGISTRY.registerItem("boot", BasicItem::new);

    //swamp
    DeferredItem<Item> SLUDGE_CATFISH = FISH_REGISTRY.registerItem("sludge_catfish", FishItem::new);
    DeferredItem<Item> LILY_SNAPPER = FISH_REGISTRY.registerItem("lily_snapper", FishItem::new);
    DeferredItem<Item> SAGE_CATFISH = FISH_REGISTRY.registerItem("sage_catfish", FishItem::new);
    DeferredItem<Item> MOSSY_BOOT = TRASH_REGISTRY.registerItem("mossy_boot", BasicItem::new);

    //darkoak_forest
    DeferredItem<Item> PALE_CARP = FISH_REGISTRY.registerItem("pale_carp", FishItem::new);
    DeferredItem<Item> PALE_PINFISH = FISH_REGISTRY.registerItem("pale_pinfish", FishItem::new);
    DeferredItem<Item> PINFISH = FISH_REGISTRY.registerItem("pinfish", FishItem::new);

    //icy lake
    DeferredItem<Item> FROSTJAW_TROUT = FISH_REGISTRY.registerItem("frostjaw_trout", FishItem::new);
    DeferredItem<Item> CRYSTALBACK_TROUT = FISH_REGISTRY.registerItem("crystalback_trout", FishItem::new);
    DeferredItem<Item> AURORA = FISH_REGISTRY.registerItem("aurora", FishItem::new);
    DeferredItem<Item> WINTERY_PIKE = FISH_REGISTRY.registerItem("wintery_pike", FishItem::new);

    //warm lake (desert/savanna etc)
    DeferredItem<Item> SANDTAIL = FISH_REGISTRY.registerItem("sandtail", FishItem::new);
    DeferredItem<Item> MIRAGE_CARP = FISH_REGISTRY.registerItem("mirage_carp", FishItem::new);
    DeferredItem<Item> SCORCHFISH = FISH_REGISTRY.registerItem("scorchfish", FishItem::new);
    DeferredItem<Item> CACTIFISH = FISH_REGISTRY.registerItem("cactifish", FishItem::new);
    DeferredItem<Item> AGAVE_BREAM = FISH_REGISTRY.registerItem("agave_bream", FishItem::new);

    //mountain
    DeferredItem<Item> SUNNY_STURGEON = FISH_REGISTRY.registerItem("sunny_sturgeon", FishItem::new);
    DeferredItem<Item> ROCKGILL = FISH_REGISTRY.registerItem("rockgill", FishItem::new);
    DeferredItem<Item> PEAKDWELLER = FISH_REGISTRY.registerItem("peakdweller", FishItem::new);
    DeferredItem<Item> SUN_SEEKING_CARP = FISH_REGISTRY.registerItem("sun_seeking_carp", FishItem::new);

    //cherry grove
    DeferredItem<Item> BLOSSOMFISH = FISH_REGISTRY.registerItem("blossomfish", FishItem::new);
    DeferredItem<Item> PETALDRIFT_CARP = FISH_REGISTRY.registerItem("petaldrift_carp", FishItem::new);
    DeferredItem<Item> PINK_KOI = FISH_REGISTRY.registerItem("pink_koi", FishItem::new);
    DeferredItem<Item> MORGANITE = FISH_REGISTRY.registerItem("morganite", FishItem::new);
    DeferredItem<Item> ROSE_SIAMESE_FISH = FISH_REGISTRY.registerItem("rose_siamese_fish", FishItem::new);
    DeferredItem<Item> VESANI = FISH_REGISTRY.registerItem("vesani", FishItem::new);

    //icy mountain
    DeferredItem<Item> CRYSTALBACK_STURGEON = FISH_REGISTRY.registerItem("crystalback_sturgeon", FishItem::new);
    DeferredItem<Item> ICETOOTH_STURGEON = FISH_REGISTRY.registerItem("icetooth_sturgeon", FishItem::new);
    DeferredItem<Item> BOREAL = FISH_REGISTRY.registerItem("boreal", FishItem::new);
    DeferredItem<Item> CRYSTALBACK_BOREAL = FISH_REGISTRY.registerItem("crystalback_boreal", FishItem::new);

    //rivers
    DeferredItem<Item> SILVERFIN_PIKE = FISH_REGISTRY.registerItem("silverfin_pike", FishItem::new);
    DeferredItem<Item> CARPENJOE = FISH_REGISTRY.registerItem("carpenjoe", FishItem::new);
    DeferredItem<Item> WILLOW_BREAM = FISH_REGISTRY.registerItem("willow_bream", FishItem::new);
    DeferredItem<Item> DRIFTING_BREAM = FISH_REGISTRY.registerItem("drifting_bream", FishItem::new);
    DeferredItem<Item> DOWNFALL_BREAM = FISH_REGISTRY.registerItem("downfall_bream", FishItem::new);
    DeferredItem<Item> HOLLOWBELLY_DARTER = FISH_REGISTRY.registerItem("hollowbelly_darter", FishItem::new);
    DeferredItem<Item> MISTBACK_CHUB = FISH_REGISTRY.registerItem("mistback_chub", FishItem::new);
    DeferredItem<Item> BLUEGIGI = FISH_REGISTRY.registerItem("bluegigi", FishItem::new);
    DeferredItem<Item> DRIED_SEAWEED = TRASH_REGISTRY.registerItem("dried_seaweed", FishItem::new);

    //icy river
    DeferredItem<Item> FROSTGILL_CHUB = FISH_REGISTRY.registerItem("frostgill_chub", FishItem::new);
    DeferredItem<Item> CRYSTALBACK_MINNOW = FISH_REGISTRY.registerItem("crystalback_minnow", FishItem::new);
    DeferredItem<Item> AZURE_CRYSTALBACK_MINNOW = FISH_REGISTRY.registerItem("azure_crystalback_minnow", FishItem::new);
    DeferredItem<Item> BLUE_CRYSTAL_FIN = FISH_REGISTRY.registerItem("blue_crystal_fin", FishItem::new);

    //saltwater
    DeferredItem<Item> IRONJAW_HERRING = FISH_REGISTRY.registerItem("ironjaw_herring", FishItem::new);
    DeferredItem<Item> DEEPJAW_HERRING = FISH_REGISTRY.registerItem("deepjaw_herring", FishItem::new);
    DeferredItem<Item> DUSKTAIL_SNAPPER = FISH_REGISTRY.registerItem("dusktail_snapper", FishItem::new);
    DeferredItem<Item> JOEL = FISH_REGISTRY.registerItem("joel", FishItem::new);
    DeferredItem<Item> REDSCALED_TUNA = FISH_REGISTRY.registerItem("redscaled_tuna", FishItem::new);
    DeferredItem<Item> BIGEYE_TUNA = FISH_REGISTRY.registerItem("bigeye_tuna", FishItem::new);
    DeferredItem<Item> SEA_BASS = FISH_REGISTRY.registerItem("sea_bass", FishItem::new);
    DeferredItem<Item> WATERLOGGED_BOTTLE = TRASH_REGISTRY.registerItem("waterlogged_bottle", BasicItem::new);

    //beaches
    DeferredItem<Item> CONCH = TRASH_REGISTRY.registerItem("conch", BasicItem::new);
    DeferredItem<Item> CLAM = TRASH_REGISTRY.registerItem("clam", BasicItem::new);

    //mushroom islands
    DeferredItem<Item> SHROOMFISH = FISH_REGISTRY.registerItem("shroomfish", FishItem::new);
    DeferredItem<Item> SPOREFISH = FISH_REGISTRY.registerItem("sporefish", FishItem::new);

    //underground
    DeferredItem<Item> GOLD_FAN = FISH_REGISTRY.registerItem("gold_fan", FishItem::new);
    DeferredItem<Item> GEODE_EEL = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("geode_eel", FishItem::new);

    //caves
    DeferredItem<Item> WHITEVEIL = FISH_REGISTRY.registerItem("whiteveil", FishItem::new);
    DeferredItem<Item> BLACK_EEL = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("black_eel", FishItem::new);
    DeferredItem<Item> AMETHYSTBACK = FISH_REGISTRY.registerItem("amethystback", FishItem::new);
    DeferredItem<Item> STONEFISH = FISH_REGISTRY.registerItem("stonefish", FishItem::new);

    //dripstone caves
    DeferredItem<Item> FOSSILIZED_ANGELFISH = FISH_REGISTRY.registerItem("fossilized_angelfish", FishItem::new);
    DeferredItem<Item> DRIPFIN = FISH_REGISTRY.registerItem("dripfin", FishItem::new);
    DeferredItem<Item> YELLOWSTONE_FISH = FISH_REGISTRY.registerItem("yellowstone_fish", FishItem::new);

    //lush caves
    DeferredItem<Item> LUSH_PIKE = FISH_REGISTRY.registerItem("lush_pike", FishItem::new);
    DeferredItem<Item> VIVID_MOSS = FISH_REGISTRY.registerItem("vivid_moss", FishItem::new);
    DeferredItem<Item> THE_QUARRISH = FISH_REGISTRY.registerItem("the_quarrish", FishItem::new);

    //deepslate
    DeferredItem<Item> GHOSTLY_PIKE = FISH_REGISTRY.registerItem("ghostly_pike", FishItem::new);
    DeferredItem<Item> AQUAMARINE_PIKE = FISH_REGISTRY.registerItem("aquamarine_pike", FishItem::new);
    DeferredItem<Item> GARNET_MACKEREL = FISH_REGISTRY.registerItem("garnet_mackerel", FishItem::new);
    DeferredItem<Item> BRIGHT_AMETHYST_SNAPPER = FISH_REGISTRY.registerItem("bright_amethyst_snapper", FishItem::new);
    DeferredItem<Item> DARK_AMETHYST_SNAPPER = FISH_REGISTRY.registerItem("dark_amethyst_snapper", FishItem::new);
    DeferredItem<Item> DEEPSLATEFISH = FISH_REGISTRY.registerItem("deepslatefish", FishItem::new);

    //deep dark
    DeferredItem<Item> SCULKFISH = FISH_REGISTRY.registerItem("sculkfish", FishItem::new);
    DeferredItem<Item> WARD = FISH_REGISTRY.registerItem("ward", FishItem::new);
    DeferredItem<Item> GLOWING_DARK = FISH_REGISTRY.registerItem("glowing_dark", FishItem::new);

    //overworld surface lava
    DeferredItem<Item> SUNEATER = FISH_REGISTRY.registerItem("suneater", FireResistantBasicItem::new);
    DeferredItem<Item> PYROTROUT = FISH_REGISTRY.registerItem("pyrotrout", FireResistantBasicItem::new);
    DeferredItem<Item> OBSIDIAN_EEL = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("obsidian_eel", FireResistantBasicItem::new);

    //overworld underground lava
    DeferredItem<Item> MOLTEN_SHRIMP = FISH_REGISTRY.registerItem("molten_shrimp", FireResistantBasicItem::new);
    DeferredItem<Item> OBSIDIAN_CRAB = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("obsidian_crab", FireResistantBasicItem::new);

    //overworld deepslate lava
    DeferredItem<Item> SCORCHED_BLOODSUCKER = FISH_REGISTRY.registerItem("scorched_bloodsucker", FireResistantBasicItem::new);
    DeferredItem<Item> MOLTEN_DEEPSLATE_CRAB = FISH_REGISTRY.registerItem("molten_deepslate_crab", FireResistantBasicItem::new);

    //nether
    DeferredItem<Item> EMBERGILL = FISH_REGISTRY.registerItem("embergill", FireResistantBasicItem::new);
    DeferredItem<Item> SCALDING_PIKE = FISH_REGISTRY.registerItem("scalding_pike", FireResistantBasicItem::new);
    DeferredItem<Item> CINDER_SQUID = FISH_REGISTRY.registerItem("cinder_squid", FireResistantBasicItem::new);
    DeferredItem<Item> LAVA_CRAB = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("lava_crab", FireResistantBasicItem::new);
    DeferredItem<Item> MAGMA_FISH = FISH_REGISTRY.registerItem("magma_fish", FireResistantBasicItem::new);
    DeferredItem<Item> GLOWSTONE_SEEKER = FISH_REGISTRY.registerItem("glowstone_seeker", FireResistantBasicItem::new);
    DeferredItem<Item> GLOWSTONE_PUFFERFISH = FISH_REGISTRY.registerItem("glowstone_pufferfish", FireResistantBasicItem::new);
    DeferredItem<Item> WILLISH = FISH_REGISTRY.registerItem("willish", FireResistantBasicItem::new);

    DeferredItem<Item> CERBERAY = FISH_REGISTRY.registerItem("cerberay", FireResistantBasicItem::new);

    DeferredItem<Item> LAVA_CRAB_CLAW = TRASH_REGISTRY.registerItem("lava_crab_claw", FireResistantBasicItem::new);

    //the end
    DeferredItem<Item> CHARFISH = FISH_REGISTRY.registerItem("charfish", FishItem::new);
    DeferredItem<Item> CHORUS_CRAB = KINDA_BUT_NOT_REALLY_FISH_REGISTRY.registerItem("chorus_crab", FishItem::new);
    DeferredItem<Item> END_GLOW = FISH_REGISTRY.registerItem("end_glow", FishItem::new);
    DeferredItem<Item> VOIDBITER = FISH_REGISTRY.registerItem("voidbiter", FishItem::new);

    //bucket
    DeferredItem<Item> STARCAUGHT_BUCKET = ITEMS_REGISTRY.registerItem("starcaught_bucket", p -> new StarcaughtBucket(p, Fluids.WATER));
}
