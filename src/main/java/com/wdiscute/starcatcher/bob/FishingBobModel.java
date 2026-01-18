package com.wdiscute.starcatcher.bob;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

// In 1.21.11, Model.renderToBuffer() and root() are final - no overriding needed
public class FishingBobModel extends EntityModel<FishingBobRenderState>
{
    // Store reference to the inner "root" child part for animations
    private final ModelPart rootPart;

    public FishingBobModel(ModelPart root) {
        // Pass a RenderType function to the parent - this is required for proper texture rendering
        super(root, rl -> RenderTypes.entityCutoutNoCull(rl));
        // The model has a child called "root" that we use for animations
        this.rootPart = root.getChild("root");
    }

    @Override
    public void setupAnim(FishingBobRenderState renderState)
    {
        // Reset pose is handled by parent, but we can do custom animation here
        this.rootPart.getAllParts().forEach(ModelPart::resetPose);
    }
}
