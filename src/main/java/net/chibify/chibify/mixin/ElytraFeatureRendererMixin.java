package net.chibify.chibify.mixin;

import net.chibify.chibify.Chibify;
import net.chibify.chibify.config.ModConfig;
import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraFeatureRendererMixin<S extends BipedEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M>  {

    public ElytraFeatureRendererMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    private void onRender(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S bipedEntityRenderState, float f, float g, CallbackInfo ci){
        Entity entity = ((IEntityRenderState) bipedEntityRenderState).chibify$getEntity();
        if (!ModConfig.INSTANCE.enabled) return;

        if (!(entity instanceof LivingEntity livingEntity) || (entity == Chibify.mc.player && !ModConfig.INSTANCE.shrinkSelf)) return;
        if (livingEntity instanceof PlayerEntity) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.translate(0f,1.42f,-0.05f);
        }
    }

}
