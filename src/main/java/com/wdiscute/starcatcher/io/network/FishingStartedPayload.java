package com.wdiscute.starcatcher.io.network;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.storage.FishProperties;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FishingStartedPayload(FishProperties fp, ItemStack rod) implements CustomPacketPayload {

    public static final Type<FishingStartedPayload> TYPE = new Type<>(Starcatcher.rl("fishing_started"));

    public static final StreamCodec<ByteBuf, FishingStartedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(FishProperties.CODEC),
            FishingStartedPayload::fp,
            ByteBufCodecs.fromCodec(ItemStack.CODEC),
            FishingStartedPayload::rod,
            FishingStartedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // Only run on client - FMLEnvironment.getDist() is safe to call on both sides
            if (FMLEnvironment.getDist() == Dist.CLIENT) {
                ClientHandler.openMinigameScreen(fp(), rod());
            }
        });
    }

    // Inner class to isolate client code - only loaded when DistExecutor runs on CLIENT
    private static class ClientHandler {
        static void openMinigameScreen(FishProperties fp, ItemStack rod) {
            net.minecraft.client.Minecraft.getInstance().setScreen(
                new com.wdiscute.starcatcher.minigame.FishingMinigameScreen(fp, rod)
            );
        }
    }
}
