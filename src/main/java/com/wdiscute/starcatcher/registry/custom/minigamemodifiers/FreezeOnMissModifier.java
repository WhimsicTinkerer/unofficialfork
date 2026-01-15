package com.wdiscute.starcatcher.registry.custom.minigamemodifiers;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class FreezeOnMissModifier extends AbstractMinigameModifier
{
    public static final Identifier OVERLAY = Starcatcher.rl("textures/gui/minigame/modifiers/freeze.png");

    @Override
    public void onMiss()
    {
        super.onMiss();

        instance.addUniqueModifier(new FrozenPointerWhileActiveModifier(40, 10));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, float partialTick, int width, int height)
    {
        super.renderBackground(guiGraphics, partialTick, width, height);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                OVERLAY, width / 2 - 48, height / 2 - 48,
                0, 0, 96, 96, 96, 96);
    }
}
