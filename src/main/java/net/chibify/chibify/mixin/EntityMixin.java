package net.chibify.chibify.mixin;

import net.chibify.chibify.Chibify;
import net.chibify.chibify.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true)
    private void modifyHeight(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (!ModConfig.INSTANCE.enabled) return;

        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (entity == Chibify.mc.player &&
                !ModConfig.INSTANCE.shrinkSelf) return;


        if (livingEntity instanceof PlayerEntity) {
            float original = cir.getReturnValue();

            cir.setReturnValue(original * 0.55f);
        }
    }

    @Inject(method = "getStandingEyeHeight()F", at = @At("RETURN"), cancellable = true)
    private void modifyEyeHeight(CallbackInfoReturnable<Float> cir) {
        if (!ModConfig.INSTANCE.enabled) return;


        Entity entity = (Entity)(Object)this;

        if (!ModConfig.INSTANCE.shrinkSelf || !ModConfig.INSTANCE.accurateEyeHeight) return;

        if (entity == Chibify.mc.player &&
                !ModConfig.INSTANCE.shrinkSelf) return;


        if (entity instanceof PlayerEntity) {
            float original = cir.getReturnValue();

            // Match your body scaling (~0.55–0.6 works best)
            cir.setReturnValue(original * 0.55f);
        }
    }

}
