package com.wdiscute.starcatcher.io;

/**
 * Measurement unit options for displaying fish sizes and weights.
 * This enum is server-safe and can be used in Config.
 * String formatting methods that use I18n are in ClientHelper.
 */
public enum Units
{
    METRIC("gui.guide.units.metric", 1f, 1f),
    IMPERIAL("gui.guide.units.imperial", 0.3937f, 0.0352739619495804f),
    CHEESEBURGER("gui.guide.units.cheeseburger", 0.09f, 0.0087f),
    FOOTBALL("gui.guide.units.football", 0.04545f, 0.00233f),
    DEVELOPER_HEIGHT("gui.guide.units.developer", 0.00592f, 0.0000140845f),
    BANANA("gui.guide.units.banana", 0.05f, 0.00833f),
    DUCK("gui.guide.units.duck", 0.02f, 0.0006667f),
    SPACE_WHALE("gui.guide.units.space_whale", 1f, 1f),
    SCIENTIFIC("gui.guide.units.scientific", 1f, 1f),
    ;

    private static final Units[] vals = values();
    private final String translationKey;
    private final float multiplierSize;
    private final float multiplierWeight;

    Units(String translationKey, float multiplierSize, float multiplierWeight)
    {
        this.translationKey = translationKey;
        this.multiplierSize = multiplierSize;
        this.multiplierWeight = multiplierWeight;
    }

    public String getTranslationKey()
    {
        return this.translationKey;
    }

    public float getMultiplierSize()
    {
        return this.multiplierSize;
    }

    public float getMultiplierWeight()
    {
        return this.multiplierWeight;
    }

    public Units next()
    {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public Units previous()
    {
        if (this.ordinal() == 0) return vals[vals.length - 1];
        return vals[(this.ordinal() - 1) % vals.length];
    }
}
