package com.wdiscute.starcatcher;

import com.wdiscute.starcatcher.io.FishSort;
import com.wdiscute.starcatcher.io.Units;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue MINIGAME_RENDER_SCALE = BUILDER
            .comment("//ALL THESE SETTINGS CAN ALSO BE ACCESSED")
            .comment("//THROUGH THE IN-GAME SETTING TAB INSIDE")
            .comment("//THE STARCATCHER'S GUIDE")
            .defineInRange("minigame_scale", 1.5, 0.1, 6);


    public static final ModConfigSpec.DoubleValue HIT_DELAY = BUILDER
            .defineInRange("hit_delay", 0.0d, -20, 20);

    public static final ModConfigSpec.EnumValue<Units> UNIT = BUILDER
            .defineEnum("units", Units.METRIC);

    public static final ModConfigSpec.EnumValue<FishSort> SORT = BUILDER
            .defineEnum("sort", FishSort.ALPHABETICAL_DOWN);

    public static final ModConfigSpec.BooleanValue VANILLA_PARTIAL_TICK = BUILDER
            .comment("Whether to use the vanilla partial ticks for minigame smoothing or a custom implementation from 1.20")
            .comment("Vanilla should look better for most people")
            .define("vanilla_partial_ticks", true);


    static final ModConfigSpec SPEC = BUILDER.build();


    private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue GIVE_GUIDE = BUILDER_SERVER
            .comment("Award guide when joining the world/server once per player")
            .define("give_guide", true);

    public static final ModConfigSpec.BooleanValue SHOW_EXCLAMATION_MARK_PARTICLE = BUILDER_SERVER
            .define("show_exclamation_mark_particle", false);

    public static final ModConfigSpec.BooleanValue ENABLE_BONE_MEAL_ON_FARMLAND_FOR_WORMS = BUILDER_SERVER
            .comment("Enables/disables the ability to bonemeal farmland for worms.")
            .define("enable_worms", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MINIGAME = BUILDER_SERVER
            .define("enable_minigame", true);

    public static final ModConfigSpec.BooleanValue ENABLE_FTB_TEAM_SHARING = BUILDER_SERVER
            .comment("Enables/disables fishes caught being unlocked for all online team members.")
            .comment("Offline players won't be awarded the entry.")
            .define("enable_seasons", false);

    public static final ModConfigSpec.BooleanValue ENABLE_SEASONS = BUILDER_SERVER
            .comment("Enables/disables fishes being restricted by seasons.")
            .comment("Useful if you want to play with a seasons mod but don't like the built-in restrictions.")
            .define("enable_seasons", true);

    static final ModConfigSpec SPEC_SERVER = BUILDER_SERVER.build();


}
