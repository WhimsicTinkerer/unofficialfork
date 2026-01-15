package com.wdiscute.starcatcher.registry;

import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemProperties
{
    // Note: In 1.21.11, item properties are defined via data-driven item models
    // rather than through code registration. This class can be used to register
    // custom property providers if needed, but the standard approach is to use
    // data-driven models in assets/<modid>/items/

    public static void addCustomItemProperties()
    {
        // In 1.21.11, item model properties are typically data-driven
        // The "cast" property for fishing rods should be defined in the item model JSON
        // If custom code-based properties are still needed, use the new APIs:
        // ConditionalItemModelProperties or RangeSelectItemModelProperties
    }
}
