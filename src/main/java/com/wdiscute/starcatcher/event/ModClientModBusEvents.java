package com.wdiscute.starcatcher.event;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.bob.FishingBobRenderer;
import com.wdiscute.starcatcher.client.CastProperty;
import com.wdiscute.starcatcher.fishentity.FishRenderer;
import com.wdiscute.starcatcher.fishentity.fishmodels.*;
import com.wdiscute.starcatcher.fishspotter.FishRadarLayer;
import com.wdiscute.starcatcher.items.BucketTooltipRenderer;
import com.wdiscute.starcatcher.items.StarcaughtBucket;
import com.wdiscute.starcatcher.particles.FishingBitingLavaParticles;
import com.wdiscute.starcatcher.particles.FishingBitingParticles;
import com.wdiscute.starcatcher.particles.FishingNotificationParticles;
import com.wdiscute.starcatcher.registry.*;
import com.wdiscute.starcatcher.registry.custom.tackleskin.*;
import com.wdiscute.starcatcher.rod.FishingRodScreen;
import com.wdiscute.starcatcher.tournament.StandScreen;
import com.wdiscute.starcatcher.tournament.TournamentOverlay;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

/**
 * Client-side events that run on the MOD event bus.
 * These include registration events like RegisterMenuScreensEvent, RegisterParticleProvidersEvent, etc.
 */
@EventBusSubscriber(modid = Starcatcher.MOD_ID, value = Dist.CLIENT)
public class ModClientModBusEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FISHING_BOB.get(), FishingBobRenderer::new);
        EntityRenderers.register(ModEntities.BOTTLE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntities.FISH.get(), FishRenderer::new);
        ModItemProperties.addCustomItemProperties();
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Starcatcher.rl("fish_tracker"), new FishRadarLayer());
        event.registerAboveAll(Starcatcher.rl("tournament"), new TournamentOverlay());
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.FISHING_NOTIFICATION.get(), FishingNotificationParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FISHING_BITING.get(), FishingBitingParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FISHING_BITING_LAVA.get(), FishingBitingLavaParticles.Provider::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.FISHING_ROD_MENU.get(), FishingRodScreen::new);
        event.register(ModMenuTypes.STAND_MENU.get(), StandScreen::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //tackle skins - using TackleSkinModels for client-only layer definitions
        event.registerLayerDefinition(TackleSkinModels.BASE_LAYER, TackleSkinModels::createBaseLayer);
        event.registerLayerDefinition(TackleSkinModels.PEARL_LAYER, TackleSkinModels::createPearlLayer);
        event.registerLayerDefinition(TackleSkinModels.KIMBE_LAYER, TackleSkinModels::createKimbeLayer);
        event.registerLayerDefinition(TackleSkinModels.FROG_LAYER, TackleSkinModels::createFrogLayer);
        event.registerLayerDefinition(TackleSkinModels.COLORFUL_LAYER, TackleSkinModels::createColorfulLayer);
        event.registerLayerDefinition(TackleSkinModels.CLEAR_LAYER, TackleSkinModels::createClearLayer);
        event.registerLayerDefinition(TackleSkinModels.KING_LAYER, TackleSkinModels::createKingLayer);

        //fishes
        event.registerLayerDefinition(AgaveBream.LAYER_LOCATION, AgaveBream::createBodyLayer);
        event.registerLayerDefinition(BigeyeTuna.LAYER_LOCATION, BigeyeTuna::createBodyLayer);
        event.registerLayerDefinition(Boreal.LAYER_LOCATION, Boreal::createBodyLayer);
        event.registerLayerDefinition(CactiFish.LAYER_LOCATION, CactiFish::createBodyLayer);
        event.registerLayerDefinition(Charfish.LAYER_LOCATION, Charfish::createBodyLayer);
        event.registerLayerDefinition(CrystalbackBoreal.LAYER_LOCATION, CrystalbackBoreal::createBodyLayer);
        event.registerLayerDefinition(CrystalbackMinnow.LAYER_LOCATION, CrystalbackMinnow::createBodyLayer);
        event.registerLayerDefinition(DeepjawHerring.LAYER_LOCATION, DeepjawHerring::createBodyLayer);
        event.registerLayerDefinition(DownfallBream.LAYER_LOCATION, DownfallBream::createBodyLayer);
        event.registerLayerDefinition(Driftfin.LAYER_LOCATION, Driftfin::createBodyLayer);
        event.registerLayerDefinition(DriftingBream.LAYER_LOCATION, DriftingBream::createBodyLayer);
        event.registerLayerDefinition(DusktailSnapper.LAYER_LOCATION, DusktailSnapper::createBodyLayer);
        event.registerLayerDefinition(LilySnapper.LAYER_LOCATION, LilySnapper::createBodyLayer);
        event.registerLayerDefinition(PinkKoi.LAYER_LOCATION, PinkKoi::createBodyLayer);
        event.registerLayerDefinition(SilverveilPerch.LAYER_LOCATION, SilverveilPerch::createBodyLayer);
        event.registerLayerDefinition(SludgeCatfish.LAYER_LOCATION, SludgeCatfish::createBodyLayer);
        event.registerLayerDefinition(Whiteveil.LAYER_LOCATION, Whiteveil::createBodyLayer);
        event.registerLayerDefinition(WillowBream.LAYER_LOCATION, WillowBream::createBodyLayer);
        event.registerLayerDefinition(WinteryPike.LAYER_LOCATION, WinteryPike::createBodyLayer);
        event.registerLayerDefinition(CrystalbackTrout.LAYER_LOCATION, CrystalbackTrout::createBodyLayer);
        event.registerLayerDefinition(Embergill.LAYER_LOCATION, Embergill::createBodyLayer);
        event.registerLayerDefinition(FrostgillChub.LAYER_LOCATION, FrostgillChub::createBodyLayer);
        event.registerLayerDefinition(FrostjawTrout.LAYER_LOCATION, FrostjawTrout::createBodyLayer);
        event.registerLayerDefinition(HollowbellyDarter.LAYER_LOCATION, HollowbellyDarter::createBodyLayer);
        event.registerLayerDefinition(IcetoothSturgeon.LAYER_LOCATION, IcetoothSturgeon::createBodyLayer);
        event.registerLayerDefinition(MistbackChub.LAYER_LOCATION, MistbackChub::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeymappings.MINIGAME_HIT);
        event.register(ModKeymappings.EXPAND_TOURNAMENT);
    }

    @SubscribeEvent
    public static void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(StarcaughtBucket.BucketTooltip.class, BucketTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        // Register the cast property for Starcatcher fishing rods
        event.register(Starcatcher.rl("cast"), CastProperty.MAP_CODEC);
    }
}
