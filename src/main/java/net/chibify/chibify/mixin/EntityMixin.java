package net.chibify.chibify.mixin;

import net.chibify.chibify.client.ChibifyClient;
import net.chibify.chibify.config.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getBbHeight()F", at = @At("RETURN"), cancellable = true)
    private void chibify$modifyHeight(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        if (ChibifyClient.shouldChibify(self)) {
            ModConfig cfg = ModConfig.INSTANCE;
            cir.setReturnValue(cir.getReturnValueF() * cfg.hitboxScale * cfg.playerScale);
        }
    }

    @Inject(method = "getEyeHeight()F", at = @At("RETURN"), cancellable = true)
    private void chibify$modifyEyeHeight(CallbackInfoReturnable<Float> cir) {
        ModConfig cfg = ModConfig.INSTANCE;
        if (!cfg.enabled || !cfg.shrinkSelf || !cfg.accurateEyeHeight) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (ChibifyClient.shouldChibify(self)) {
            cir.setReturnValue(cir.getReturnValueF() * cfg.hitboxScale * cfg.playerScale);
        }
    }

    @Inject(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", at = @At("RETURN"), cancellable = true)
    private void chibify$modifyPhysicalDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        ModConfig cfg = ModConfig.INSTANCE;
        if (!cfg.shrinkPhysicalHitbox) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (ChibifyClient.shouldChibify(self)) {
            float heightFactor = cfg.hitboxScale * cfg.playerScale;
            float widthFactor = cfg.limbWidthScale * cfg.playerScale;
            cir.setReturnValue(cir.getReturnValue().scale(widthFactor, heightFactor));
        }
    }
}
