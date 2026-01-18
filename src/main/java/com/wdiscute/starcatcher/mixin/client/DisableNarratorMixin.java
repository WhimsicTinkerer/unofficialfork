package com.wdiscute.starcatcher.mixin.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameNarrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to disable the narrator on macOS to prevent a crash in the native text2speech library.
 * The crash occurs in NarratorMac.setDelegate() when using JNA on certain macOS versions.
 */
@Mixin(GameNarrator.class)
public class DisableNarratorMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"))
    private static Narrator redirectGetNarrator() {
        // Return a no-op narrator instead of the native one to prevent macOS crash
        return Narrator.EMPTY;
    }
}
