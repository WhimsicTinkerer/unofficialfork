package com.wdiscute.starcatcher.registry.custom.tackleskin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wdiscute.starcatcher.bob.FishingBobModel;
import com.wdiscute.starcatcher.bob.FishingBobRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-only helper for rendering tackle skins.
 * This class handles all the client-specific rendering code to keep AbstractTackleSkin server-safe.
 */
public class TackleSkinRenderHelper
{
    private static final Map<Identifier, RenderData> renderDataCache = new HashMap<>();

    private static class RenderData
    {
        RenderType renderType;
        FishingBobModel model;
    }

    public static void submitTackle(
            AbstractTackleSkin skin,
            EntityRendererProvider.Context context,
            FishingBobRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int packedLight)
    {
        Identifier textureId = skin.getTexture();
        RenderData data = renderDataCache.computeIfAbsent(textureId, k -> new RenderData());

        if (data.renderType == null)
        {
            // Construct ModelLayerLocation from the identifier
            ModelLayerLocation layerLoc = new ModelLayerLocation(skin.getLayerLocationId(), "main");
            data.model = new FishingBobModel(context.bakeLayer(layerLoc));
            data.renderType = RenderTypes.entityCutout(textureId);
        }

        data.model.setupAnim(renderState);
        collector.submitModel(data.model, renderState, poseStack, data.renderType, packedLight, OverlayTexture.NO_OVERLAY, -1, null);
    }
}
