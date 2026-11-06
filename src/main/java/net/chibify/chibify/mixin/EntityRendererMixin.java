package net.chibify.chibify.mixin;

import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Attaches the source entity to every freshly extracted render state.
 *
 * InventoryScreen builds its preview with EntityRenderer#createRenderState directly,
 * bypassing EntityRenderDispatcher#extractEntity. Attaching here makes the same
 * Chibify model transforms available to world rendering and inventory previews.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void chibify$attachEntityToRenderState(
        Entity entity,
        EntityRenderState state,
        float partialTicks,
        CallbackInfo ci
    ) {
        if (state instanceof IEntityRenderState chibifyState) {
            chibifyState.chibify$setEntity(entity);
            chibifyState.chibify$setModelScaled(false);
        }
    }
}
