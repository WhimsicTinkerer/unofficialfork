package com.wdiscute.starcatcher.io.network.tournament;


import com.mojang.authlib.GameProfile;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.tournament.StandScreen;
import com.wdiscute.starcatcher.tournament.Tournament;
import com.wdiscute.starcatcher.tournament.TournamentOverlay;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record CBActiveTournamentUpdatePayload(List<GameProfile> listSignups, Tournament tour) implements CustomPacketPayload
{

    public static CBActiveTournamentUpdatePayload helper(Player player, Tournament tournament)
    {
        if (player.level().isClientSide()) throw new RuntimeException();
        List<GameProfile> list = new ArrayList<>();
        for (var entry : tournament.playerScores)
        {
            // GameProfileCache was removed in 1.21.11, use PlayerList to get online players
            ServerPlayer serverPlayer = player.level().getServer().getPlayerList().getPlayer(entry.playerUUID);
            if (serverPlayer != null)
            {
                list.add(serverPlayer.getGameProfile());
            }
            else
            {
                // Fallback: create a minimal profile with just the UUID if player is offline
                list.add(new GameProfile(entry.playerUUID, "Unknown"));
            }
        }

        return new CBActiveTournamentUpdatePayload(list, tournament);
    }

    public static final StreamCodec<ByteBuf, GameProfile> GAME_PROFILE_STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, GameProfile::id,
            ByteBufCodecs.STRING_UTF8, GameProfile::name,
            GameProfile::new
    );

    public static final StreamCodec<ByteBuf, List<GameProfile>> GAME_PROFILE_STREAM_CODEC_LIST = GAME_PROFILE_STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final Type<CBActiveTournamentUpdatePayload> TYPE = new Type<>(Starcatcher.rl("cb_clear_tournament"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CBActiveTournamentUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            GAME_PROFILE_STREAM_CODEC_LIST, CBActiveTournamentUpdatePayload::listSignups,
            Tournament.STREAM_CODEC, CBActiveTournamentUpdatePayload::tour,
            CBActiveTournamentUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }


    public void handle(IPayloadContext context)
    {
        TournamentOverlay.onTournamentReceived(tour, listSignups);
    }

}
