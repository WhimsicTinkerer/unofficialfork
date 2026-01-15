package com.wdiscute.starcatcher.client;

import com.wdiscute.starcatcher.Config;
import com.wdiscute.starcatcher.guide.FishCaughtToast;
import com.wdiscute.starcatcher.io.Units;
import com.wdiscute.starcatcher.storage.FishProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

/**
 * Client-only helper class for operations that require client classes.
 * This class should NEVER be referenced from common/server code.
 */
public class ClientHelper
{
    public static void fishCaughtToast(FishProperties fp, boolean newFish, int sizeCM, int weightCM)
    {
        if (newFish) Minecraft.getInstance().getToastManager().addToast(new FishCaughtToast(fp));

        Units units = Config.UNIT.get();

        String size = getSizeAsString(units, sizeCM);
        String weight = getWeightAsString(units, weightCM);

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("")
                        .append(Component.translatable(fp.catchInfo().fish().value().getDescriptionId()))
                        .append(Component.literal(" - " + size + " - " + weight))
                , true);

        Minecraft.getInstance().gui.overlayMessageTime = 180;
    }

    /**
     * Format size value as string using the given units.
     * Uses I18n for translations - client-only.
     */
    public static String getSizeAsString(Units units, int sizeInCm)
    {
        if (units == Units.SPACE_WHALE) return "\u221e space whales";
        if (units == Units.SCIENTIFIC) return "0 AU";

        float size = sizeInCm * units.getMultiplierSize();
        String sizeString = ((float) (int) (size * 100)) / 100 + " " + I18n.get(units.getTranslationKey() + ".size");

        if (units == Units.METRIC)
        {
            sizeString = ((int) size) + "cm";
            if (size > 100) sizeString = (float) ((int) (size / 100 * 100)) / 100 + "m";
        }

        if (units == Units.IMPERIAL)
        {
            sizeString = ((int) size) + "''";
            if (size > 12) sizeString = ((int) (size / 12)) + "'" + ((int) (size % 12)) + "''";
        }

        return sizeString;
    }

    /**
     * Format weight value as string using the given units.
     * Uses I18n for translations - client-only.
     */
    public static String getWeightAsString(Units units, int weightInGrams)
    {
        if (units == Units.SPACE_WHALE) return "\u221e space whales";
        if (units == Units.SCIENTIFIC) return "0 R136a1's";

        float weight = weightInGrams * units.getMultiplierWeight();
        String weightString = ((float) (int) (weight * 100)) / 100 + " " + I18n.get(units.getTranslationKey() + ".weight");

        if (units == Units.METRIC)
        {
            if (weight < 1000) weightString = ((int) weight) + "g";
            if (weight > 1000) weightString = (float) ((int) (weight / 1000 * 100)) / 100 + "kg";
        }

        if (units == Units.IMPERIAL)
        {
            weightString = ((int) weight) + "oz";
            if (weight > 12) weightString = ((int) (weight / 16)) + " lb " + ((int) (weight % 16)) + " oz";
        }

        return weightString;
    }
}
