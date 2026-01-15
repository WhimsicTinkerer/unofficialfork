package com.wdiscute.starcatcher.secretnotes;

import com.wdiscute.libtooltips.Tooltips;
import com.wdiscute.starcatcher.Starcatcher;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SecretNoteScreen extends Screen
{
    private static final Identifier BACKGROUND = Starcatcher.rl("textures/gui/secret_note.png");

    private final String translationKey;

    int uiX;
    int uiY;

    @Override
    protected void init()
    {
        super.init();
        uiX = (width - 512) / 2;
        uiY = (height - 256) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderImage(guiGraphics, BACKGROUND);

        for (int i = 0; i < 20; i++)
        {
            String key = translationKey + i;
            if (I18n.exists(key))
            {
                guiGraphics.drawString(this.font, Tooltips.decodeTranslationKey(key), uiX + 140, uiY + 55 + 9 * i, 0x635040, false);
            }
            else
            {
                break;
            }
        }
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

    public SecretNoteScreen(SecretNote.Note note)
    {
        super(Component.empty());
        this.translationKey = "gui.secret_note." + note.getSerializedName() + ".";
    }

    private void renderImage(GuiGraphics guiGraphics, Identifier rl)
    {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, rl, uiX, uiY, 0, 0, 512, 256, 512, 256);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
