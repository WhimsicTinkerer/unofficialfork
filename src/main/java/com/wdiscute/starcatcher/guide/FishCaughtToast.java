package com.wdiscute.starcatcher.guide;

import com.wdiscute.libtooltips.Tooltips;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.U;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class FishCaughtToast implements Toast
{
    private static final Identifier BACKGROUND_SPRITE = Starcatcher.rl("toast/fish_caught");
    private final Component title;
    private final String description;
    private static final String gibberish = "§kaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private int old;
    private final ItemStack is;
    private final String pre;
    private final String post;

    public FishCaughtToast(FishProperties fp)
    {
        this.is = new ItemStack(fp.catchInfo().fish());
        this.title = Component.translatable("gui.starcatcher.toast.fish_caught");
        this.description = is.getHoverName().getString();

        pre = fp.rarity().getPre();
        post = fp.rarity().getPost();
    }

    @Override
    public int width()
    {
        return 164;
    }

    @Override
    public int height()
    {
        return 51;
    }

    private long lastTime = 0;
    private Visibility visibility = Visibility.SHOW;

    @Override
    public Visibility getWantedVisibility()
    {
        return visibility;
    }

    @Override
    public void update(ToastManager toastManager, long timeSinceLastVisible)
    {
        lastTime = timeSinceLastVisible;
        int lettersRevealed = (int) Math.clamp((timeSinceLastVisible - 500) / 150, 0, description.length());

        if (old != lettersRevealed)
        {
            Minecraft.getInstance().player.playSound(SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON, 0.4f, U.r.nextFloat(0.2f) + 1.3f);
            old = lettersRevealed;
        }

        visibility = timeSinceLastVisible < 10000 ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long timeSinceLastVisible)
    {
        guiGraphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, width(), height());

        guiGraphics.renderItem(is, 6, 29);

        guiGraphics.drawString(font, this.title, 40, 13, 0x635040, false);

        int lettersRevealed = (int) Math.clamp((timeSinceLastVisible - 500) / 150, 0, description.length());

        Component comp = Tooltips.decodeString(pre + description.substring(0, lettersRevealed) + post).copy()
                .append(Component.literal(gibberish.substring(0, description.length() - lettersRevealed + 2)).withStyle(Style.EMPTY.withColor(0x635040)));

        guiGraphics.drawString(font, comp, 40, 22, 0x635040, false);
    }

}
