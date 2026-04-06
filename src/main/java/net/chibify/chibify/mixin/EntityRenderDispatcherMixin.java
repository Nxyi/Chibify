package net.chibify.chibify.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderManager.class)
public abstract class EntityRenderDispatcherMixin {

    @ModifyExpressionValue(
            method = "getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;")    )
    private <E extends Entity> EntityRenderState getAndUpdateRenderState$setEntity(EntityRenderState state, E entity, float tickProgress) {
        ((IEntityRenderState) state).chibify$setEntity(entity);
        return state;
    }
}