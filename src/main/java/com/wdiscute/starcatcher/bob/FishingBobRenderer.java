package com.wdiscute.starcatcher.bob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.wdiscute.starcatcher.Starcatcher;
import com.wdiscute.starcatcher.StarcatcherTags;
import com.wdiscute.starcatcher.io.ModDataAttachments;
import com.wdiscute.starcatcher.registry.custom.tackleskin.AbstractTackleSkin;
import com.wdiscute.starcatcher.registry.custom.tackleskin.BaseTackleSkin;
import com.wdiscute.starcatcher.registry.custom.tackleskin.ITackleSkin;
import com.wdiscute.starcatcher.registry.custom.tackleskin.TackleSkinRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

// In 1.21.11, EntityRenderer uses submit() instead of render()
public class FishingBobRenderer extends EntityRenderer<FishingBobEntity, FishingBobRenderState>
{
    final EntityRendererProvider.Context context;
    private final Map<Identifier, AbstractTackleSkin> tackleSkinCache = new HashMap<>();

    public FishingBobRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.context = context;
    }

    private AbstractTackleSkin getTackleSkin(Identifier rl)
    {
        if (rl == null)
        {
            return tackleSkinCache.computeIfAbsent(Starcatcher.rl("base"), k -> new BaseTackleSkin());
        }
        return tackleSkinCache.computeIfAbsent(rl, key -> {
            if (Minecraft.getInstance().level != null)
            {
                Optional<Supplier<ITackleSkin>> optional = Minecraft.getInstance().level
                        .registryAccess()
                        .lookupOrThrow(Starcatcher.TACKLE_SKIN)
                        .getOptional(key);
                if (optional.isPresent())
                {
                    // All ITackleSkin implementations extend AbstractTackleSkin
                    return (AbstractTackleSkin) optional.get().get();
                }
            }
            return new BaseTackleSkin();
        });
    }

    @Override
    public FishingBobRenderState createRenderState()
    {
        return new FishingBobRenderState();
    }

    @Override
    public void extractRenderState(FishingBobEntity entity, FishingBobRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.entityYaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.owner = entity.getOwner() instanceof Player p ? p : null;
        state.tackleSkinTexture = ModDataAttachments.get(entity, ModDataAttachments.TACKLE_SKIN);
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        // Precompute line origin offset like vanilla does
        if (state.owner != null)
        {
            Player player = state.owner;
            float f = player.getAttackAnim(partialTick);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTick);
            Vec3 vec31 = entity.getPosition(partialTick).add(0.0, 0.25, 0.0);
            state.lineOriginOffset = vec3.subtract(vec31);
        }
        else
        {
            state.lineOriginOffset = Vec3.ZERO;
        }
    }

    @Override
    public void submit(FishingBobRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState)
    {
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.entityYaw));

        // Render the tackle skin model using the client-only render helper
        AbstractTackleSkin tackleSkin = getTackleSkin(state.tackleSkinTexture);
        TackleSkinRenderHelper.submitTackle(tackleSkin, context, state, poseStack, collector, state.lightCoords);

        poseStack.popPose();

        // Render fishing line from bobber to player
        if (state.owner != null)
        {
            float f = (float) state.lineOriginOffset.x;
            float f1 = (float) state.lineOriginOffset.y;
            float f2 = (float) state.lineOriginOffset.z;
            float lineWidth = Minecraft.getInstance().getWindow().getAppropriateLineWidth();

            collector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, consumer) -> {
                for (int j = 0; j < 16; j++)
                {
                    float stringFraction = fraction(j, 16);
                    float nextStringFraction = fraction(j + 1, 16);
                    stringVertex(f, f1, f2, consumer, pose, stringFraction, nextStringFraction, lineWidth);
                    stringVertex(f, f1, f2, consumer, pose, nextStringFraction, stringFraction, lineWidth);
                }
            });
        }

        super.submit(state, poseStack, collector, cameraState);
    }

    private static void stringVertex(
        float x, float y, float z,
        VertexConsumer consumer,
        PoseStack.Pose pose,
        float stringFraction,
        float nextStringFraction,
        float lineWidth
    )
    {
        float f = x * stringFraction;
        float f1 = y * (stringFraction * stringFraction + stringFraction) * 0.5F + 0.25F;
        float f2 = z * stringFraction;
        float f3 = x * nextStringFraction - f;
        float f4 = y * (nextStringFraction * nextStringFraction + nextStringFraction) * 0.5F + 0.25F - f1;
        float f5 = z * nextStringFraction - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        consumer.addVertex(pose, f, f1, f2).setColor(-16777216).setNormal(pose, f3, f4, f5).setLineWidth(lineWidth);
    }

    private static float fraction(int numerator, int denominator)
    {
        return (float) numerator / (float) denominator;
    }

    private Vec3 getPlayerHandPos(Player player, float p_340872_, float partialTick)
    {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemstack = player.getMainHandItem();
        if (!itemstack.is(StarcatcherTags.RODS))
        {
            i = -i;
        }

        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player)
        {
            double d4 = 960.0 / (double) this.entityRenderDispatcher.options.fov().get().intValue();
            Vec3 vec3 = this.entityRenderDispatcher
                    .camera
                    .getNearPlane()
                    .getPointOnPlane((float) i * 0.525F, -0.1F)
                    .scale(d4)
                    .yRot(p_340872_ * 0.5F)
                    .xRot(-p_340872_ * 0.7F);
            return player.getEyePosition(partialTick).add(vec3);
        }
        else
        {
            float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
            double d0 = (double) Mth.sin(f);
            double d1 = (double) Mth.cos(f);
            float f1 = player.getScale();
            double d2 = (double) i * 0.35 * (double) f1;
            double d3 = 0.8 * (double) f1;
            float f2 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double) f2 - 0.45 * (double) f1, -d0 * d2 + d1 * d3);
        }
    }
}
