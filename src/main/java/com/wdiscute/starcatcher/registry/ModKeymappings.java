package com.wdiscute.starcatcher.registry;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeymappings {
    // TODO: Register custom category in RegisterKeyMappingsEvent when needed
    // For now using GAMEPLAY category
    public static final KeyMapping MINIGAME_HIT = new KeyMapping("key.starcatcher.minigame_hit", GLFW.GLFW_KEY_SPACE, KeyMapping.Category.GAMEPLAY);
    public static final KeyMapping EXPAND_TOURNAMENT = new KeyMapping("key.starcatcher.expand_tournament", GLFW.GLFW_KEY_TAB, KeyMapping.Category.GAMEPLAY);
}
