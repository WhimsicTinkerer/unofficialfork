package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.minigame.FishingMinigameScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.sounds.SoundEvents;

public class FrozenPointerWhileActiveModifier extends AbstractTimedModifier
{
    private final int rampTime;

    public FrozenPointerWhileActiveModifier(int length, int rampTime)
    {
        super(length);
        this.length = length;
        this.rampTime = rampTime;
    }

    @Override
    public void onAdd(FishingMinigameScreen instance)
    {
        super.onAdd(instance);
        Minecraft.getInstance().player.playSound(SoundEvents.GLASS_BREAK, 0.4f, 1f);
        Minecraft.getInstance().player.playSound(SoundEvents.SNOW_BREAK, 1f, 1f);
    }

    @Override
    public void tick()
    {
        super.tick();
        float currentSpeed = instance.pointerSpeed;

        float decreaseTime = Math.abs(instance.pointerBaseSpeed) / rampTime;

        if(tickCount <= rampTime)
        {
            instance.pointerSpeed = Math.abs(currentSpeed) < decreaseTime ? 0 : currentSpeed - Math.signum(currentSpeed) * decreaseTime;
        }

        if(tickCount >= length - rampTime)
        {
            float newPointerSpeed = currentSpeed + U.sign(currentSpeed) * decreaseTime;
            instance.pointerSpeed = Math.abs(instance.pointerBaseSpeed) < newPointerSpeed ? instance.pointerBaseSpeed : newPointerSpeed;
        }
    }

    @Override
    public void onRemove()
    {
        super.onRemove();
        instance.pointerSpeed = instance.pointerBaseSpeed;
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, float partialTick, int width, int height)
    {
        super.renderForeground(guiGraphics, partialTick, width, height);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FishingMinigameScreen.TEXTURE, width / 2 - 16, height / 2 - 16, 0, 0, 32, 32, 256, 256);
    }
}
