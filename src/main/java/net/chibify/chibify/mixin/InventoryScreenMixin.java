package net.chibify.chibify.mixin;

import net.chibify.chibify.client.ChibifyClient;
import net.chibify.chibify.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

/**
 * Keeps the local player's inventory preview centered after Chibify changes
 * the vertical proportions of the humanoid model.
 *
 * Both the survival inventory and the Creative inventory's Inventory tab call
 * InventoryScreen.extractEntityInInventoryFollowsMouse, so one hook covers both.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @ModifyArgs(
        method = "extractEntityInInventoryFollowsMouse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;entity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3fc;Lorg/joml/Quaternionfc;Lorg/joml/Quaternionfc;IIII)V"
        )
    )
    private static void chibify$centerSelfPreview(Args args) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft == null ? null : minecraft.player;
        if (player == null || !ChibifyClient.shouldChibify(player)) {
            return;
        }

        EntityRenderState renderState = args.get(0);
        Vector3fc oldTranslation = args.get(2);

        // Preserve the vanilla offset supplied by Survival/Creative, but do not
        // use the player's gameplay hitbox height as the preview anchor. Chibify
        // can optionally change that height for collision/eye behaviour, while
        // the inventory preview should remain anchored to the visual model.
        float originalOffsetY = oldTranslation.y() - (renderState.boundingBoxHeight * 0.5F);
        float vanillaPlayerHeight = player.getType().getDimensions().height();

        ModConfig cfg = ModConfig.INSTANCE;
        float overall = cfg.playerScale;
        float headScale = cfg.headScale * overall;
        float limbHeight = cfg.limbHeightScale * overall;
        float limbWidth = cfg.limbWidthScale * overall;

        // Mirror the pivot calculations in BipedEntityModelMixin. The standard
        // unscaled model spans model-Y -12..24, whose midpoint is 6 pixels.
        // Recalculate its vertical bounds from the configured Chibi geometry.
        float legPivotY = 24.0F - (12.0F * limbHeight);
        float torsoPivotY = legPivotY - (12.0F * limbHeight);
        float headTopY = torsoPivotY - (8.5F * headScale); // includes hat inflation
        float bodyTopY = torsoPivotY - (12.0F * limbHeight);
        float topY = Math.min(headTopY, bodyTopY);
        float bottomY = 24.0F;

        // The crouching pose rotates the torso and moves the legs up slightly.
        // Approximate the rotated torso's vertical extent using its Y/Z half-sizes.
        if (player.isCrouching()) {
            float sin = (float) Math.sin(0.5F);
            float cos = (float) Math.cos(0.5F);
            float bodyTopCrouching = torsoPivotY - (12.0F * limbHeight * cos)
                - (2.0F * limbWidth * sin);
            float bodyBottomCrouching = torsoPivotY + (2.0F * limbWidth * sin);
            topY = Math.min(topY, bodyTopCrouching);
            bottomY = Math.max(bodyBottomCrouching, 24.0F - overall);
        }

        float chibiCenterY = (topY + bottomY) * 0.5F;
        float vanillaModelCenterY = 6.0F;
        float modelCenterDelta = (chibiCenterY - vanillaModelCenterY) / 16.0F;

        // Use the original player type's height for a stable anchor, then add
        // the difference between the Chibi model center and vanilla model center.
        // User-adjustable fine tuning for the inventory preview only. This is
        // deliberately applied after the automatic geometry-based centering so
        // users can make a small personal correction without changing gameplay.
        float centeredY = (vanillaPlayerHeight * 0.5F) + originalOffsetY + modelCenterDelta
            + cfg.inventoryPreviewOffsetY;
        args.set(2, new Vector3f(oldTranslation.x(), centeredY, oldTranslation.z()));
    }
}
