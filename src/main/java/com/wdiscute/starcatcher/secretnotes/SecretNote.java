package com.wdiscute.starcatcher.secretnotes;

import com.mojang.serialization.Codec;
import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public class SecretNote extends Item
{
    public SecretNote(Item.Properties props)
    {
        super(props.stacksTo(1).component(ModDataComponents.SECRET_NOTE, Note.SAMPLE_NOTE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand)
    {
        if(level.isClientSide() && FMLEnvironment.getDist() == Dist.CLIENT)
        {
            ClientHandler.openScreen(ModDataComponents.get(player.getItemInHand(usedHand), ModDataComponents.SECRET_NOTE));
        }
        return super.use(level, player, usedHand);
    }

    // Inner class to isolate client code - only loaded when on CLIENT
    private static class ClientHandler
    {
        static void openScreen(Note note)
        {
            net.minecraft.client.Minecraft.getInstance().setScreen(new SecretNoteScreen(note));
        }
    }

    public enum Note implements StringRepresentable
    {
        SAMPLE_NOTE("sample_note"),
        CRYSTAL_HOOK("crystal_hook"),
        ARNWULF_1("lava_proof_bottle_1"),
        ARNWULF_2("lava_proof_bottle_2"),
        HOPEFUL_NOTE("hopeful_note"),
        HOPELESS_NOTE("hopeless_note"),
        WITHER("wither_note"),
        TRUE_BLUE("true_blue");

        public static final Codec<Note> CODEC = StringRepresentable.fromEnum(Note::values);
        public static final StreamCodec<FriendlyByteBuf, Note> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Note.class);
        private final String key;

        Note(String key)
        {
            this.key = key;
        }

        public @NotNull String getSerializedName()
        {
            return this.key;
        }
    }


}


