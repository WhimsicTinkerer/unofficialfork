package com.wdiscute.starcatcher.compat;

import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Re-enable when FTB Teams 1.21.11 is available
// Original imports:
// import dev.ftb.mods.ftbteams.api.Team;
// import dev.ftb.mods.ftbteams.data.TeamManagerImpl;

public class FTBTeamsCompat
{
    private static final Logger log = LoggerFactory.getLogger(FTBTeamsCompat.class);

    // Stub implementation - does nothing when mod is not available
    public static void awardToTeam(Player player, FishProperties fp)
    {
        // No-op: FTB Teams not available for 1.21.11 yet
    }
}
