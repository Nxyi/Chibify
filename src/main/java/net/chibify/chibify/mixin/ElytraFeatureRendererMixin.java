package net.chibify.chibify.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.chibify.chibify.client.ChibifyClient;
import net.chibify.chibify.config.ModConfig;
import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public abstract class ElytraFeatureRendererMixin<S extends HumanoidRenderState, M extends EntityModel<S>>
    extends RenderLayer<S, M> {

    public ElytraFeatureRendererMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
            shift = At.Shift.AFTER
        )
    )
    private void chibify$scaleAndAlignElytra(
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int lightCoords,
        S state,
        float yRot,
        float xRot,
        CallbackInfo ci
    ) {
        if (!(state instanceof IEntityRenderState duck)) {
            return;
        }
        Entity entity = duck.chibify$getEntity();
        if (!ChibifyClient.shouldChibify(entity)) {
            return;
        }

        ModConfig cfg = ModConfig.INSTANCE;
        float scale = cfg.elytraScale * cfg.playerScale;

        // Preserve the transform order used by the original Chibify 1.3.1
        // renderer: scale around the Elytra attachment origin first, then apply
        // a small local-space nudge. The previous FIXAT5-FIXAT8 revisions moved
        // the Elytra by a full humanoid-model pivot (roughly 10-12 model units),
        // which can push it into/behind the self-chibi body and make it vanish.
        // Leg clearance now shortens the lower wing span gently without moving
        // the attachment point or changing the X/Z width.
        float clearance = Math.max(0.0F, Math.min(3.0F, cfg.elytraLegClearance));
        float verticalFactor = Math.max(0.70F, 1.0F - (clearance * 0.10F));

        poseStack.scale(scale, scale * verticalFactor, scale);
        poseStack.translate(
            0.0F,
            1.42F + cfg.elytraOffsetY,
            cfg.elytraOffsetZ
        );
    }
}
