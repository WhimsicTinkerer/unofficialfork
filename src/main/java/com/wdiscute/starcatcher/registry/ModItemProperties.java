package com.wdiscute.starcatcher.registry;

/**
 * Item properties are now data-driven in 1.21.11.
 * This class is kept for compatibility but does nothing.
 * Called from ModClientEvents on the client side only.
 */
public class ModItemProperties
{
    public static void addCustomItemProperties()
    {
        // In 1.21.11, item model properties are typically data-driven
        // The "cast" property for fishing rods should be defined in the item model JSON
    }
}
