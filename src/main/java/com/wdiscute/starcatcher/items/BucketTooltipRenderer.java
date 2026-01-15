package com.wdiscute.starcatcher.items;

import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.client.ClientHelper;
import com.wdiscute.starcatcher.io.ModDataComponents;
import com.wdiscute.starcatcher.io.SizeAndWeightInstance;
import com.wdiscute.starcatcher.io.Units;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BucketTooltipRenderer implements ClientTooltipComponent {
    public StarcaughtBucket.BucketTooltip tooltip;
    public String text = "";

    public BucketTooltipRenderer(StarcaughtBucket.BucketTooltip tooltip){
        this.tooltip = tooltip;

        if (ModDataComponents.has(tooltip.fish(),ModDataComponents.SIZE_AND_WEIGHT)) {
            SizeAndWeightInstance sw = ModDataComponents.get(tooltip.fish(), ModDataComponents.SIZE_AND_WEIGHT);

            Units units = Config.UNIT.get();

            String size = ClientHelper.getSizeAsString(units, sw.sizeInCentimeters());
            String weight = ClientHelper.getWeightAsString(units, sw.weightInGrams());

            this.text = size + " - " + weight;
        }

    }

    @Override
    public int getHeight(Font font) {
        return isEmpty() ? 0 : 18;
    }

    @Override
    public int getWidth(Font font) {
        if (isEmpty()) return 0;

        int ret = 16 + Math.round(text.length() * 5.8f);
        return hasProperties() ? ret : 16 ;
    }

    // Note: render method signature changed in 1.21.11
    public void render(GuiGraphics guiGraphics, Font font, int x, int y) {
        if (!isEmpty()) {
            guiGraphics.renderItem(tooltip.fish(), x, y);

            if (hasProperties())
                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(text), x + 20, y + 4, 0x888888,true);
        }
    }

    public boolean isEmpty() {
        return tooltip.fish().isEmpty();
    }

    public boolean hasProperties() {
        if (isEmpty()) return false;
        return ModDataComponents.has(tooltip.fish(),ModDataComponents.SIZE_AND_WEIGHT);
    }
}
