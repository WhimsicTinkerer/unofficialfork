package com.wdiscute.starcatcher.io;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.tournament.Tournament;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

public class TournamentSavedData extends SavedData
{
    public static final String NAME = "tournaments";

    public static final Codec<TournamentSavedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Tournament.CODEC.listOf().fieldOf("tournaments").forGetter(data -> data.tournaments)
            ).apply(instance, TournamentSavedData::new)
    );

    public static final SavedDataType<TournamentSavedData> TYPE = new SavedDataType<>(
            NAME,
            TournamentSavedData::new,
            CODEC
    );

    private List<Tournament> tournaments = new ArrayList<>();

    public TournamentSavedData(List<Tournament> tournaments)
    {
        this.tournaments = new ArrayList<>(tournaments);
    }

    public TournamentSavedData()
    {
        this.tournaments = new ArrayList<>();
    }

    public static TournamentSavedData get(ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public List<Tournament> getTournaments()
    {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments)
    {
        this.tournaments = new ArrayList<>(tournaments);
        setDirty();
    }
}
