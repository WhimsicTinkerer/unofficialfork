package com.wdiscute.starcatcher.compat;

import com.wdiscute.starcatcher.storage.FishProperties;
import com.wdiscute.starcatcher.storage.FishProperties.WorldRestrictions.Seasons;
import net.minecraft.world.level.Level;

// TODO: Re-enable when EclipticSeasons 1.21.11 is available
// Original imports:
// import com.teamtea.eclipticseasons.api.constant.solar.Season;
// import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
// import com.teamtea.eclipticseasons.api.util.EclipticUtil;

public class EclipticSeasonsCompat
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
