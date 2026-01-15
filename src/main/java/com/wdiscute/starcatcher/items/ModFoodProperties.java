package com.wdiscute.starcatcher.items;

import com.wdiscute.starcatcher.registry.ModItems;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties
{
    public static final FoodProperties BASIC_RAW_FISH = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.1f)
            // Note: usingConvertsTo was removed in 1.21.11
            .build();

}
