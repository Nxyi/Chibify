package net.chibify.chibify.mixin;

import net.chibify.chibify.client.ChibifyClient;
import net.chibify.chibify.config.ModConfig;
import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends HumanoidRenderState> extends EntityModel<T> {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart hat;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    protected BipedEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void chibify$applyChibiProportions(T state, CallbackInfo ci) {
        if (!(state instanceof IEntityRenderState duck)) {
            return;
        }
        Entity entity = duck.chibify$getEntity();
        if (!ChibifyClient.shouldChibify(entity)) {
            return;
        }

        ModConfig cfg = ModConfig.INSTANCE;
        float overall = cfg.playerScale;
        float hScale = cfg.headScale * overall;
        float wScale = cfg.limbWidthScale * overall;
        float lHeight = cfg.limbHeightScale * overall;

        // Ground-contact pivot math (Minecraft model ground plane = Y 24.0):
        // Default lHeight = 0.5 -> legPivotY = 18.0, torsoTopY = 12.0, armPivotY = 13.0.
        float legPivotY = 24.0F - (12.0F * lHeight);
        float torsoTopY = legPivotY - (12.0F * lHeight);
        float armPivotY = torsoTopY + (2.0F * lHeight);

        // Inward X shifts matching Chibify 1.3.1 defaults (wScale=0.7 -> arm +1.45, leg +0.55)
        float armInwardShift = (1.0F - cfg.limbWidthScale) * (1.45F / 0.3F) * overall;
        float legInwardShift = (1.0F - cfg.limbWidthScale) * (0.55F / 0.3F) * overall;

        // Head & Hat (hat is a child of head in 1.21.2+ / 26.3, so hat.y = 0.0F stays locked)
        this.head.xScale = hScale;
        this.head.yScale = hScale;
        this.head.zScale = hScale;
        this.head.y = torsoTopY;
        this.hat.y = 0.0F;

        // Torso
        this.body.xScale = wScale;
        this.body.yScale = lHeight;
        this.body.zScale = wScale;
        this.body.y = torsoTopY;

        // Right Arm
        this.rightArm.xScale = wScale;
        this.rightArm.yScale = lHeight;
        this.rightArm.zScale = wScale;
        this.rightArm.y = armPivotY;
        this.rightArm.x += armInwardShift;

        // Left Arm
        this.leftArm.xScale = wScale;
        this.leftArm.yScale = lHeight;
        this.leftArm.zScale = wScale;
        this.leftArm.y = armPivotY;
        this.leftArm.x -= armInwardShift;

        // Left Leg
        this.leftLeg.xScale = wScale;
        this.leftLeg.yScale = lHeight;
        this.leftLeg.zScale = wScale;
        this.leftLeg.y = legPivotY;
        this.leftLeg.x -= legInwardShift;

        // Right Leg
        this.rightLeg.xScale = wScale;
        this.rightLeg.yScale = lHeight;
        this.rightLeg.zScale = wScale;
        this.rightLeg.y = legPivotY;
        this.rightLeg.x += legInwardShift;

        // Crouching adjustments matching 1.3.1
        if (state.isCrouching) {
            this.body.xRot = 0.5F;
            this.rightLeg.y -= 1.0F * overall;
            this.rightLeg.z -= 1.5F * overall;
            this.leftLeg.y -= 1.0F * overall;
            this.leftLeg.z -= 1.5F * overall;
        }

        duck.chibify$setModelScaled(true);
    }
}
