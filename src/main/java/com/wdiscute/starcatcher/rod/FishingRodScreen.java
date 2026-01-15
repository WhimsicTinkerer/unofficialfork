package com.wdiscute.starcatcher.rod;

import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.input.KeyEvent;

public class FishingRodScreen extends AbstractContainerScreen<FishingRodMenu>
{
    private static final Identifier BACKGROUND = Starcatcher.rl("textures/gui/rod_screen.png");


    public FishingRodScreen(FishingRodMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        // Check if inventory key was pressed
        if (this.minecraft.options.keyInventory.matches(event))
        {
            this.onClose();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1)
    {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
