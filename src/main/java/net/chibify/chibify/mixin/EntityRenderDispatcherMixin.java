package net.chibify.chibify.mixin;

import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "extractEntity", at = @At("RETURN"))
    private <E extends Entity, S extends EntityRenderState> void chibify$attachEntityToState(
        E entity, float partialTick, CallbackInfoReturnable<S> cir
    ) {
        S state = cir.getReturnValue();
        if (state instanceof IEntityRenderState duck) {
            duck.chibify$setEntity(entity);
            duck.chibify$setModelScaled(false);
        }
    }
}
