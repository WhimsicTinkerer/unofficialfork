package com.wdiscute.starcatcher.compat;

import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.FishProperties.WorldRestrictions.Seasons;
import net.minecraft.world.level.Level;

// TODO: Re-enable when TerraFirmaCraft 1.21.11 is available
// Original imports:
// import net.dries007.tfc.util.calendar.Calendars;
// import net.dries007.tfc.util.calendar.Month;
// import net.dries007.tfc.util.calendar.Season;

public class TerraFirmaCraftSeasonsCompat
{
    // Stub implementation - always returns true when mod is not available
    public static boolean canCatch(FishProperties fp, Level level)
    {
        return true;
    }

    public static Seasons getSeason(Level level)
    {
        return Seasons.ALL;
    }

    public static Seasons getSubSeason(Level level)
    {
        return Seasons.ALL;
    }
}
