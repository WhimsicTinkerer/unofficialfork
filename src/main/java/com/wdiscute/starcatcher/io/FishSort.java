package com.wdiscute.starcatcher.io;

import net.neoforged.fml.ModList;

/**
 * Sorting options for the fishing guide.
 * This enum is server-safe and can be used in Config.
 */
public enum FishSort
{
    ALPHABETICAL_UP("gui.guide.sort.alphabetical_up"),
    ALPHABETICAL_DOWN("gui.guide.sort.alphabetical_down"),
    MOD_UP("gui.guide.sort.mod_up"),
    MOD_DOWN("gui.guide.sort.mod_down"),
    RARITY_UP("gui.guide.sort.rarity_up"),
    RARITY_DOWN("gui.guide.sort.rarity_down"),
    CAUGHT_UP("gui.guide.sort.caught_up"),
    CAUGHT_DOWN("gui.guide.sort.caught_down"),
    FLUID_UP("gui.guide.sort.fluid_up"),
    FLUID_DOWN("gui.guide.sort.fluid_down"),
    SEASON_UP("gui.guide.sort.season_up"),
    SEASON_DOWN("gui.guide.sort.season_down");

    private static final FishSort[] vals = values();

    private final String translationKey;

    FishSort(String translationKey)
    {
        this.translationKey = translationKey;
    }

    public String getTranslationKey()
    {
        return this.translationKey;
    }

    /**
     * Get the next sort option. Skips SEASON options if seasons mods aren't loaded.
     */
    public FishSort next()
    {
        // Default length excludes SEASON_UP and SEASON_DOWN
        int length = vals.length - 2;
        // Include season options if a seasons mod is loaded
        if (ModList.get().isLoaded("sereneseasons") || ModList.get().isLoaded("eclipticseasons"))
        {
            length += 2;
        }
        return vals[(this.ordinal() + 1) % length];
    }

    /**
     * Get the previous sort option. Skips SEASON options if seasons mods aren't loaded.
     */
    public FishSort previous()
    {
        // Default length excludes SEASON_UP and SEASON_DOWN
        int length = vals.length - 2;
        // Include season options if a seasons mod is loaded
        if (ModList.get().isLoaded("sereneseasons") || ModList.get().isLoaded("eclipticseasons"))
        {
            length += 2;
        }
        if (this.ordinal() == 0) return vals[length - 1];
        return vals[(this.ordinal() - 1) % length];
    }
}
