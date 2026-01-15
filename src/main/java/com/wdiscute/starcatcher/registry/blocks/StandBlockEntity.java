package com.wdiscute.starcatcher.registry.blocks;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher.tournament.StandMenu;
import com.wdiscute.starcatcher.tournament.Tournament;
import com.wdiscute.starcatcher.tournament.TournamentHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StandBlockEntity extends AbstractMultiBlockEntity implements MenuProvider
{
    public Tournament tournament;
    public Map<UUID, String> profiles;
    private UUID uuid;

    public final ItemStackHandler entryCost = new ItemStackHandler(9)
    {
        @Override
        protected int getStackLimit(int slot, ItemStack stack)
        {
            return 64;
        }

    };

    public StandBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(ModBlockEntities.STAND.get(), pos, blockState);
    }

    public Tournament makeOrGetTournament()
    {
        if (tournament != null) return tournament;

        Tournament t = TournamentHandler.getTournamentOrNull(getUuid());
        if (t != null)
        {
            tournament = t;
            return tournament;
        }

        tournament = Tournament.empty(getUuid());
        return tournament;
    }

    public UUID getUuid()
    {
        if (this.uuid == null)
        {
            setUuid(UUID.randomUUID());
        }
        return this.uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
        sync();
    }

    public void sync()
    {
        setChanged();

        if (level instanceof ServerLevel serverLevel)
        {
            serverLevel.sendBlockUpdated(getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
    {
        return new StandMenu(i, inventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        if (!isCenter()) return;

        if (uuid != null)
            output.store("tournament_uuid", UUIDUtil.CODEC, uuid);

        output.storeNullable("tournament", Tournament.CODEC, tournament);
        StarcatcherGameProfileCache cache = gameProfilesHelper(level, tournament);
        output.store("profiles", StarcatcherGameProfileCache.GAME_PROFILES_CODEC, cache);
    }

    public record StarcatcherGameProfileCache(Map<UUID, String> map)
    {
        public static final Codec<StarcatcherGameProfileCache> GAME_PROFILES_CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING).fieldOf("map").forGetter(StarcatcherGameProfileCache::map)
                        ).apply(instance, StarcatcherGameProfileCache::new)
                );
    }

    public static StarcatcherGameProfileCache gameProfilesHelper(Level level, Tournament tournament)
    {
        if (level.isClientSide()) return new StarcatcherGameProfileCache(new HashMap<>());
        if (tournament == null) return new StarcatcherGameProfileCache(new HashMap<>());

        Map<UUID, String> map = new HashMap<>();
        tournament.playerScores.forEach(entry ->
        {
            // GameProfileCache was removed in 1.21.11, use PlayerList to get online players
            ServerPlayer serverPlayer = level.getServer().getPlayerList().getPlayer(entry.playerUUID);
            if (serverPlayer != null)
            {
                GameProfile profile = serverPlayer.getGameProfile();
                map.put(profile.id(), profile.name());
            }
            else
            {
                // Fallback for offline players
                map.put(entry.playerUUID, "Unknown");
            }
        });

        return new StarcatcherGameProfileCache(map);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        if (!isCenter()) return;

        input.read("tournament_uuid", UUIDUtil.CODEC).ifPresent(u -> uuid = u);
        input.read("tournament", Tournament.CODEC).ifPresent(t -> tournament = t);
        input.read("profiles", StarcatcherGameProfileCache.GAME_PROFILES_CODEC).ifPresent(cache -> profiles = cache.map);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName()
    {
        return Component.empty();
    }
}
