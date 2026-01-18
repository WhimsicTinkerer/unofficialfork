package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wdiscute.starcatcher.Starcatcher;
import org.joml.Matrix3x2fStack;
import com.wdiscute.starcatcher.minigame.ActiveSweetSpot;
import com.wdiscute.starcatcher.minigame.FishingMinigameScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class Nikdo53Modifier extends AbstractMinigameModifier
{
    public static final Identifier POINTER_SMALL = Starcatcher.rl("textures/gui/minigame/modifiers/nikdo53_pointer_1.png");
    public static final Identifier POINTER_LARGE = Starcatcher.rl("textures/gui/minigame/modifiers/nikdo53_pointer_2.png");
    public static final Identifier WHEEL = Starcatcher.rl("textures/gui/minigame/modifiers/nikdo53_wheel.png");

    public int pointerLayer = 0;
    public int maxPointerLayer;
    public boolean isHoldingLeft = false;
    public boolean isHoldingRight = false;

    Options options = Minecraft.getInstance().options;

    public Nikdo53Modifier(){
        maxPointerLayer = 1;
    }

    // This one probably shouldn't be used, but it sure is funny
    public Nikdo53Modifier(int maxPointerLayer){
       this.maxPointerLayer = maxPointerLayer;
    }

    @Override
    public boolean onHit(ActiveSweetSpot spot) {
        if (getSpotLayer(spot) == pointerLayer) {
            putSpotLayer(spot, getRandomLayer());
            return super.onHit(spot);
        }
        return true;
    }

    @Override
    public void onKeyReleased(int key, int scanCode, int keyModifiers) {
        if (key == options.keyLeft.getKey().getValue()) {
            isHoldingLeft = false;
        }

        if (key == options.keyRight.getKey().getValue()){
            isHoldingRight = false;
        }

    }

    @Override
    public void onKeyPress(int key, int scanCode, int keyModifiers) {
        if (key == options.keyLeft.getKey().getValue()) {
            pointerLayer--;
            isHoldingLeft = true;
        }

       if (key == options.keyRight.getKey().getValue()){
           pointerLayer++;
           isHoldingRight = true;
       }

       if (pointerLayer > maxPointerLayer)
           pointerLayer = maxPointerLayer;

       if (pointerLayer < 0)
           pointerLayer = 0;
    }

    @Override
    public ActiveSweetSpot onSpotAdded(ActiveSweetSpot spot) {
        int layer = getRandomLayer();
        putSpotLayer(spot, layer);
        return super.onSpotAdded(spot);
    }

    private int getRandomLayer() {
        return Minecraft.getInstance().level.getRandom().nextIntBetweenInclusive(0, maxPointerLayer);
    }

    @Override
    public void renderOnPointer(GuiGraphics guiGraphics, Matrix3x2fStack poseStack, float partialTick) {
        if (pointerLayer == 0) {
            FishingMinigameScreen.renderPoseCentered(guiGraphics, POINTER_SMALL, 128);
        } else {
            FishingMinigameScreen.renderPoseCentered(guiGraphics, POINTER_LARGE, 128);
        }
    }


    @Override
    public void renderOnSweetSpot(GuiGraphics guiGraphics, Matrix3x2fStack poseStack, ActiveSweetSpot spot, float partialTick) {
        if (spot.behaviour == null) return;

        poseStack.pushMatrix();

        int layer = getSpotLayer(spot);

        poseStack.translate(0, -9 * layer);

        // Dim when not in use
        // TODO: RenderSystem.setShaderColor was removed in 1.21.11, find replacement
        // if (pointerLayer != layer)
        //     RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);

        spot.behaviour.render(guiGraphics, poseStack, partialTick);

        // RenderSystem.setShaderColor(1, 1, 1, 1);

        poseStack.popMatrix();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, float partialTick, int width, int height) {
        super.renderBackground(guiGraphics, partialTick, width, height);
        Matrix3x2fStack poseStack = guiGraphics.pose();

        //render A
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FishingMinigameScreen.TEXTURE, width / 2 - 40, height / 2 + 40, isHoldingLeft ? 32 : 0, 128, 32, 16, 256, 256);

        //render D
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FishingMinigameScreen.TEXTURE, width / 2 + 8, height / 2 + 40, isHoldingRight ? 32 : 0, 144, 32, 16, 256, 256);


        poseStack.pushMatrix();

        // kapiten reference!1!1!1!1!!
        poseStack.translate(width >> 1, height >> 1);

        // Dim when not in use
        // TODO: RenderSystem.setShaderColor was removed in 1.21.11, find replacement
        // if (pointerLayer != 1)
        //     RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);

        FishingMinigameScreen.renderPoseCentered(guiGraphics, WHEEL, 128);

        // RenderSystem.setShaderColor(1, 1, 1, 1);

        poseStack.popMatrix();
    }

    @Override
    public boolean disablePointerRendering() {
        return true;
    }

    @Override
    public boolean disableSweetSpotRendering(ActiveSweetSpot spot) {
        return true;
    }

    private static int getSpotLayer(ActiveSweetSpot spot) {
        return (int) spot.extraData.get(53);
    }

    private static void putSpotLayer(ActiveSweetSpot spot, int layer) {
        spot.extraData.put(53, layer);
    }
}
