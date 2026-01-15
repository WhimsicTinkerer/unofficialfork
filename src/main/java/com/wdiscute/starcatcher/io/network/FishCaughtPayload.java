package com.wdiscute.starcatcher.io.network;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.storage.FishProperties;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FishCaughtPayload(FishProperties fp, boolean newFish, int size, int weight) implements CustomPacketPayload {

    public static final Type<FishCaughtPayload> TYPE = new Type<>(Starcatcher.rl("fish_caught"));

    public static final StreamCodec<ByteBuf, FishCaughtPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(FishProperties.CODEC),
            FishCaughtPayload::fp,
            ByteBufCodecs.BOOL,
            FishCaughtPayload::newFish,
            ByteBufCodecs.INT,
            FishCaughtPayload::size,
            ByteBufCodecs.INT,
            FishCaughtPayload::weight,
            FishCaughtPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // Only run on client - FMLEnvironment.getDist() is safe to call on both sides
            if (FMLEnvironment.getDist() == Dist.CLIENT) {
                ClientHandler.showToast(fp(), newFish(), size(), weight());
            }
        });
    }

    // Inner class to isolate client code - only loaded when DistExecutor runs on CLIENT
    private static class ClientHandler {
        static void showToast(FishProperties fp, boolean newFish, int size, int weight) {
            com.wdiscute.starcatcher.client.ClientHelper.fishCaughtToast(fp, newFish, size, weight);
        }
    }
}
