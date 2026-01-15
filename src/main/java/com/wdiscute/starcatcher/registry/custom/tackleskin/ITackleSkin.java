package com.wdiscute.starcatcher.registry.custom.tackleskin;

import net.minecraft.world.entity.player.Player;

/**
 * Server-safe interface for tackle skin sound callbacks.
 * The rendering methods are in AbstractTackleSkin (client-only).
 */
public interface ITackleSkin
{
    void onCast(Player player);
    void onRetrieve(Player player);
    void onMissed(Player player);
    void onSuccessfulMinigame(Player player);
    void onFailedMinigame(Player player);
}
