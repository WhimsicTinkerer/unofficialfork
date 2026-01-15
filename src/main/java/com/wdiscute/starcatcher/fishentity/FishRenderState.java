package com.wdiscute.starcatcher.fishentity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

// EntityRenderState for FishEntity in 1.21.11
public class FishRenderState extends EntityRenderState
{
    public float entityYaw;
    public boolean isInWater;
    public ItemStack bodyArmorItem = ItemStack.EMPTY;
    public int tickCount;
    public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
}
