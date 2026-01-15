package com.wdiscute.starcatcher.bob;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

// EntityRenderState for FishingBobEntity in 1.21.11
public class FishingBobRenderState extends EntityRenderState
{
    public float entityYaw;
    public Player owner;
    public Identifier tackleSkinTexture;
    public Vec3 lineOriginOffset = Vec3.ZERO;
    public int lightCoords;
}
