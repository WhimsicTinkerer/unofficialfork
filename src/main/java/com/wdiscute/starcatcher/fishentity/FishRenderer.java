package com.wdiscute.starcatcher.fishentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wdiscute.starcatcher.registry.ModItems;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.fishentity.fishmodels.*;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.HashMap;
import java.util.Map;

// In 1.21.11, EntityRenderer requires 2 type parameters: Entity and RenderState
public class FishRenderer extends EntityRenderer<FishEntity, FishRenderState>
{
    private final ItemModelResolver itemModelResolver;
    // In 1.21.11, fish models extend Model directly (no animation needed)
    Map<Item, Model<Object>> map = new HashMap<>();

    public FishRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        itemModelResolver = context.getItemModelResolver();
        map.put(ModItems.AGAVE_BREAM.get(), new AgaveBream(context.bakeLayer(AgaveBream.LAYER_LOCATION)));
        map.put(ModItems.BIGEYE_TUNA.get(), new BigeyeTuna(context.bakeLayer(BigeyeTuna.LAYER_LOCATION)));
        map.put(ModItems.BOREAL.get(), new Boreal(context.bakeLayer(Boreal.LAYER_LOCATION)));
        map.put(ModItems.CACTIFISH.get(), new CactiFish(context.bakeLayer(CactiFish.LAYER_LOCATION)));
        map.put(ModItems.CHARFISH.get(), new Charfish(context.bakeLayer(Charfish.LAYER_LOCATION)));
        map.put(ModItems.CRYSTALBACK_BOREAL.get(), new CrystalbackBoreal(context.bakeLayer(CrystalbackBoreal.LAYER_LOCATION)));
        map.put(ModItems.CRYSTALBACK_MINNOW.get(), new CrystalbackMinnow(context.bakeLayer(CrystalbackMinnow.LAYER_LOCATION)));
        map.put(ModItems.DEEPJAW_HERRING.get(), new DeepjawHerring(context.bakeLayer(DeepjawHerring.LAYER_LOCATION)));
        map.put(ModItems.DOWNFALL_BREAM.get(), new DownfallBream(context.bakeLayer(DownfallBream.LAYER_LOCATION)));
        map.put(ModItems.DRIFTFIN.get(), new Driftfin(context.bakeLayer(Driftfin.LAYER_LOCATION)));
        map.put(ModItems.DRIFTING_BREAM.get(), new DriftingBream(context.bakeLayer(DriftingBream.LAYER_LOCATION)));
        map.put(ModItems.DUSKTAIL_SNAPPER.get(), new DusktailSnapper(context.bakeLayer(DusktailSnapper.LAYER_LOCATION)));
        map.put(ModItems.LILY_SNAPPER.get(), new LilySnapper(context.bakeLayer(LilySnapper.LAYER_LOCATION)));
        map.put(ModItems.PINK_KOI.get(), new PinkKoi(context.bakeLayer(PinkKoi.LAYER_LOCATION)));
        map.put(ModItems.SILVERVEIL_PERCH.get(), new SilverveilPerch(context.bakeLayer(SilverveilPerch.LAYER_LOCATION)));
        map.put(ModItems.SLUDGE_CATFISH.get(), new SludgeCatfish(context.bakeLayer(SludgeCatfish.LAYER_LOCATION)));
        map.put(ModItems.WHITEVEIL.get(), new Whiteveil(context.bakeLayer(Whiteveil.LAYER_LOCATION)));
        map.put(ModItems.WILLOW_BREAM.get(), new WillowBream(context.bakeLayer(WillowBream.LAYER_LOCATION)));
        map.put(ModItems.WINTERY_PIKE.get(), new WinteryPike(context.bakeLayer(WinteryPike.LAYER_LOCATION)));
        map.put(ModItems.CRYSTALBACK_TROUT.get(), new CrystalbackTrout(context.bakeLayer(CrystalbackTrout.LAYER_LOCATION)));
        map.put(ModItems.EMBERGILL.get(), new Embergill(context.bakeLayer(Embergill.LAYER_LOCATION)));
        map.put(ModItems.FROSTGILL_CHUB.get(), new FrostgillChub(context.bakeLayer(FrostgillChub.LAYER_LOCATION)));
        map.put(ModItems.FROSTJAW_TROUT.get(), new FrostjawTrout(context.bakeLayer(FrostjawTrout.LAYER_LOCATION)));
        map.put(ModItems.HOLLOWBELLY_DARTER.get(), new HollowbellyDarter(context.bakeLayer(HollowbellyDarter.LAYER_LOCATION)));
        map.put(ModItems.ICETOOTH_STURGEON.get(), new IcetoothSturgeon(context.bakeLayer(IcetoothSturgeon.LAYER_LOCATION)));
        map.put(ModItems.MISTBACK_CHUB.get(), new MistbackChub(context.bakeLayer(MistbackChub.LAYER_LOCATION)));
    }

    @Override
    public FishRenderState createRenderState()
    {
        return new FishRenderState();
    }

    @Override
    public void extractRenderState(FishEntity entity, FishRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.entityYaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.isInWater = entity.isInWater();
        state.bodyArmorItem = entity.getBodyArmorItem();
        state.tickCount = entity.tickCount;

        // Update item render state for fallback rendering
        if (!state.bodyArmorItem.isEmpty() && !map.containsKey(state.bodyArmorItem.getItem()))
        {
            itemModelResolver.updateForTopItem(state.itemRenderState, state.bodyArmorItem, ItemDisplayContext.FIXED, entity.level(), null, entity.getId());
        }
    }

    @Override
    public void submit(FishRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState)
    {
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.3F, 0.0F);
        poseStack.scale(1.0F, -1.0F, -1.0F);

        poseStack.mulPose(Axis.YP.rotationDegrees(state.entityYaw));

        if (!state.isInWater)
        {
            float f = 4.3F * Mth.sin(1F * state.tickCount);
            poseStack.translate(1.1F, 1.4F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(f));
        }

        if (!state.bodyArmorItem.isEmpty())
        {
            if (!renderCustomModel(state.bodyArmorItem.getItem(), poseStack, collector, state.lightCoords))
            {
                poseStack.translate(0F, 1F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
                // Render item using the new ItemStackRenderState API
                state.itemRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            }

        }

        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }


    private boolean renderCustomModel(Item fish, PoseStack poseStack, SubmitNodeCollector collector, int packedLight)
    {
        if(map.containsKey(fish))
        {
            renderModel(BuiltInRegistries.ITEM.getKey(fish).getPath(), map.get(fish), collector, poseStack, packedLight);
            return true;
        }
        return false;
    }

    private void renderModel(String rl, Model<Object> model, SubmitNodeCollector collector, PoseStack poseStack, int packedLight)
    {
        Identifier texture = Starcatcher.rl("textures/entity/fishes/" + rl + ".png");
        // Use submitModel for model rendering in 1.21.11
        collector.submitModel(model, null, poseStack, RenderTypes.entityCutoutNoCull(texture), packedLight, OverlayTexture.NO_OVERLAY, -1, null);
    }


}
