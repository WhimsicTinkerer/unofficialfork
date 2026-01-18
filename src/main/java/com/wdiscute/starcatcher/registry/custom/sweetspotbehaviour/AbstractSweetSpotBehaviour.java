package com.wdiscute.starcatcher.registry.custom.sweetspotbehaviour;

import com.wdiscute.starcatcher.minigame.ActiveSweetSpot;
import org.joml.Matrix3x2fStack;
import com.wdiscute.starcatcher.minigame.FishingMinigameScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

public abstract class AbstractSweetSpotBehaviour
{
    public int ticksActive;
    protected FishingMinigameScreen instance;
    protected ActiveSweetSpot ass;

    public void onAdd(FishingMinigameScreen instance, ActiveSweetSpot ass)
    {
        this.instance = instance;
        this.ass = ass;
        ass.pos = instance.getRandomFreePosition(ass.thickness);
    }

    public void tick()
    {
        ticksActive++;

        ass.pos += ass.movingRate * ass.currentRotation;
        if (ass.pos > 360) ass.pos -= 360;
        if (ass.pos < 0) ass.pos += 360;

        ass.alpha -= ass.vanishingRate;

        if (ass.shouldSudokuOnVanish && ass.alpha <= 0) ass.removed = true;
    }

    public void onHit()
    {
    }

    public void onRemove()
    {
    }

    public void renderForeground(GuiGraphics guiGraphics, float partialTick, int width, int height)
    {
    }

    public void render(GuiGraphics guiGraphics, Matrix3x2fStack poseStack, float partialTick)
    {
        if (ass.removed) return;

        // Create color with alpha
        int color = ARGB.color((int)(ass.alpha * 255), 255, 255, 255);

        // Renders the sprite centered to the top-left corner of the screen, to be moved with poseStack
        FishingMinigameScreen.renderPoseCentered(guiGraphics, ass.texture, 96, color);
    }
}
