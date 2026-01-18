package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.wdiscute.starcatcher.minigame.ActiveSweetSpot;
import org.joml.Matrix3x2fStack;
import com.wdiscute.starcatcher.minigame.FishingMinigameScreen;
import net.minecraft.client.gui.GuiGraphics;

public abstract class AbstractMinigameModifier
{
    public boolean removed = false;
    public int tickCount = 0;
    protected FishingMinigameScreen instance;

    public void onAdd(FishingMinigameScreen instance)
    {
        this.instance = instance;
    }

    /**
     * Runs when removed or the minigame ends
     */
    public void onRemove(){}

    /**
     * Transforms an ActiveSweetSpot before it gets added.
     * Setting spot removed to true cancels it
     */
    public ActiveSweetSpot onSpotAdded(ActiveSweetSpot spot){
        return spot;
    }

    /**
     * Runs right before the sweetSpotBehaviour hit
     * @return whether the hit should be canceled
     */
    public boolean onHit(ActiveSweetSpot ass){
        return false;
    };

    public void onMiss(){}

    public void tick(){
        tickCount++;
    }

    public void onKeyPress(int key,  int scanCode, int keyModifiers){}

    public void onKeyReleased(int key, int scanCode, int keyModifiers){}

    public void renderBackground(GuiGraphics guiGraphics, float partialTick, int width, int height){};

    public void renderForeground(GuiGraphics guiGraphics, float partialTick, int width, int height){};

    /**
     * Disables rendering the included pointer
     * <p>
     * Still renders {@link #renderOnPointer(GuiGraphics, Matrix3x2fStack, float)}
     */
    public boolean disablePointerRendering(){
        return false;
    }

    /**
     * Has the correctly rotated poseStack already
     */
    public void renderOnPointer(GuiGraphics guiGraphics, Matrix3x2fStack poseStack, float partialTick){};


    public boolean disableSweetSpotRendering(ActiveSweetSpot spot){
        return false;
    }

    public void renderOnSweetSpot(GuiGraphics guiGraphics, Matrix3x2fStack poseStack, ActiveSweetSpot spot, float partialTick){

    };

    public boolean forceAwardTreasure()
    {
        return false;
    }
}
