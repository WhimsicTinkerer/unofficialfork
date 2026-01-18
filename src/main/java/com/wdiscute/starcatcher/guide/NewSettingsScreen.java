package com.wdiscute.starcatcher.guide;

import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.io.Units;
import com.wdiscute.starcatcher.minigame.FishingMinigameScreen;
import com.wdiscute.starcatcher.registry.custom.minigamemodifiers.AbstractMinigameModifier;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class NewSettingsScreen extends FishingMinigameScreen {
    public static final Identifier TEXTURE = Starcatcher.rl("textures/gui/minigame/minigame.png");
    public static final Identifier TANK = Starcatcher.rl("textures/gui/minigame/surface.png");
    public static final Identifier SETTINGS = Starcatcher.rl("textures/gui/minigame/settings.png");
    public static final Identifier GUI_SCALE = Starcatcher.rl("textures/gui/minigame/gui_scale.png");

    boolean changeRotation;
    boolean moveMarkers = false;
    boolean isHoveringWidgets = false;

    Units unitSelected;

    public NewSettingsScreen(FishProperties fp, ItemStack rod) {
        super(fp, rod);
    }

    @Override
    protected void init() {
        super.init();

        hitDelay = (Config.HIT_DELAY.get().floatValue());
        unitSelected = Config.UNIT.get();

        //Use widgets instead of doing hovering/clicking logic manually
        // addRenderableWidget(new GuiScaleWidget(width / 2 - 50, 0, 100, 50));


        //new gui scale
        addRenderableWidget(new LeftRightButtonWidget<>(
                () -> renderScale, // the value to render
                () -> renderScale -= 0.1f, // left button action
                () -> renderScale += 0.1f, // right button action
                0.2f, //lower limit
                5.9f, //upper limit
                Component.literal("Scale"),
                width / 2 + 100, height / 2 - 90, 91, 19, 160, 69, 256, 256, SETTINGS, 13));


        //hit delay
        addRenderableWidget(new LeftRightButtonWidget<>(
                () -> hitDelay, // the value to render
                () -> hitDelay -= 0.2f, // left button action
                () -> hitDelay += 0.2f, // right button action
                -5f,  //lower limit
                5f, //upper limit
                Component.literal("Hit Delay"),
                width / 2 + 100, height / 2 - 40, 91, 19, 160, 69, 256, 256, SETTINGS, 13));

        //Speed
        addRenderableWidget(new LeftRightButtonWidget<>(
                () -> pointerSpeed, // the value to render
                () -> pointerSpeed -= 0.1f, // left button action
                () -> pointerSpeed += 0.1f, // right button action
                null,
                null,
                Component.literal("Speed"),
                width / 2 + 100, height / 2 + 10, 91, 19, 160, 69, 256, 256, SETTINGS, 13));


        //Units
        addRenderableWidget(new LeftRightButtonWidget<>(
                () -> unitSelected, // the value to render
                () -> unitSelected = unitSelected.previous(), // left button action
                () -> unitSelected = unitSelected.next(), // right button action
                null,
                null,
                Component.literal("Units"),
                width / 2, height / 2 + 80, 136, 25, 34, 222, 256, 256, SETTINGS, 16));


        //TODO: make a boolean / buttonList widget for the little buttons
    }

    public Options getOptions() {
        return getMinecraft().options;
    }

    private @NotNull OptionInstance<Integer> guiScale() {
        return getOptions().guiScale();
    }

    @Override
    public boolean isSettingsScreen() {
        return true;
    }

    @Override
    public void inputPressed() {
        if (!(isHoldingMouse && children().stream().anyMatch(GuiEventListener::isFocused)))
            super.inputPressed();

        if (progress > 100) progress = 100;
        if (progress < 0 ) progress = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (progress > 100) progress = 100;
        if (progress < 0 ) progress = 0;
    }

    @Override
    public void onClose() {
        //round it to 2 decimal points
        Config.HIT_DELAY.set(Math.round(hitDelay * 10) / 10d);
        Config.HIT_DELAY.save();


        Config.MINIGAME_RENDER_SCALE.set((double) renderScale);
        Config.MINIGAME_RENDER_SCALE.save();

        Config.UNIT.set(unitSelected);
        Config.UNIT.save();

        modifiers.forEach(AbstractMinigameModifier::onRemove);

        this.minecraft.popGuiLayer();
    }

    public class LeftRightButtonWidget<T extends Comparable<T>> extends AbstractWidget {
        int uOffset, vOffset, textureWidth, textureHeight, buttonWidth;
        Identifier texture;
        Supplier<T> value;
        @Nullable T rightLimit, leftLimit;
        Runnable rightAction, leftAction;
        Component name;

        // This is automatically centered
        public LeftRightButtonWidget(Supplier<T> value, Runnable leftAction, Runnable rightAction, @Nullable T leftLimit, @Nullable T rightLimit, MutableComponent name,
                                     int x, int y, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight, Identifier texture, int buttonWidth) {

            super(x - (width >> 1), y - (height >> 1), width, height, Component.empty());

            this.uOffset = uOffset;
            this.vOffset = vOffset;
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.buttonWidth = buttonWidth;

            this.rightAction = rightAction;
            this.leftAction = leftAction;
            this.rightLimit = rightLimit;
            this.leftLimit = leftLimit;

            this.value = value;
            this.name = name;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // Draw button texture first
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                    texture, getX(), getY(),
                    uOffset, vOffset, getWidth(), getHeight(), textureWidth, textureHeight);

            // Then draw text on top
            Object o = value.get();
            if (o instanceof Float number){
               number = Math.round(number * 10) / 10f;
               o = number;
            }

            MutableComponent component = Component.empty().append(name).append(": ").append(String.valueOf(o));
            guiGraphics.drawCenteredString(getMinecraft().font, component, getX() + (getWidth() / 2), getY() + (getHeight() / 4), 0xff000000);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
            double mouseX = event.x();
            double mouseY = event.y();
            if (!this.isMouseOver(mouseX, mouseY)) return false;

            //left button
            if (mouseX < getX() + buttonWidth){
                if (leftLimit != null && value.get().compareTo(leftLimit) <= 0) return true;

                leftAction.run();
                return true;
            }


            //right button
            if (mouseX > getRight() - buttonWidth){
                if (rightLimit != null && value.get().compareTo(rightLimit) >= 0) return true;

                rightAction.run();
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }


    public class GuiScaleWidget extends AbstractWidget {
        public GuiScaleWidget(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());

            if (hasDistantHorizons()) {
                setTooltip(Tooltip.create(Component.literal("GUI Scale is not supported while Distant Horizons is installed. It causes a massive frame drop upon starting and ending the minigame.")));
            } else {
                setTooltip(Tooltip.create(Component.literal("Change the GUI Scale of the minigame.")));
            }
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            //GUI SCALE
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                    GUI_SCALE, getX(), getY(),
                    0, 0, getWidth(), getHeight(), getWidth(), getHeight());

        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
            double mouseX = event.x();
            double mouseY = event.y();
            if (!this.isMouseOver(mouseX, mouseY)) return false;

            int current = guiScale().get();

            // if it's on the right half
            if (mouseX < getX() + getWidth() / 2f) {
                if (current > 1)
                    guiScale().set(current - 1);

            } else {
                guiScale().set(current + 1);
            }
            return true;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }

}
