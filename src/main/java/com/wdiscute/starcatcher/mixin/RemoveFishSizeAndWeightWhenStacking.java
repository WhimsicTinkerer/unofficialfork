package com.wdiscute.starcatcher.mixin;

import com.wdiscute.starcatcher.io.ModDataComponents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class RemoveFishSizeAndWeightWhenStacking
{

    @Inject(at = @At("HEAD"), method = "overrideOtherStackedOnMe")
    private void stackedOnMe(ItemStack stack, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir)
    {
        ItemStack thisItem = (ItemStack) (Object)this;

        if(stack.is(thisItem.getItem()))
        {
            ModDataComponents.remove(stack, ModDataComponents.SIZE_AND_WEIGHT);
            ModDataComponents.remove(stack, ModDataComponents.FISH_PROPERTIES);
            ModDataComponents.remove(thisItem, ModDataComponents.SIZE_AND_WEIGHT);
            ModDataComponents.remove(thisItem, ModDataComponents.FISH_PROPERTIES);
        }

    }

    @Inject(at = @At("HEAD"), method = "overrideStackedOnOther")
    private void stackedOnMe(Slot slot, ClickAction action, Player player, CallbackInfoReturnable<Boolean> cir)
    {
        ItemStack itemBeingClickedOn = slot.getItem();
        ItemStack itemInHand = (ItemStack) (Object)this;

        if(itemBeingClickedOn.is(itemInHand.getItem()))
        {
            ModDataComponents.remove(itemInHand,  ModDataComponents.SIZE_AND_WEIGHT);
            ModDataComponents.remove(itemInHand, ModDataComponents.FISH_PROPERTIES);
            ModDataComponents.remove(itemBeingClickedOn,ModDataComponents.SIZE_AND_WEIGHT);
            ModDataComponents.remove(itemBeingClickedOn, ModDataComponents.FISH_PROPERTIES);
        }
    }

}
