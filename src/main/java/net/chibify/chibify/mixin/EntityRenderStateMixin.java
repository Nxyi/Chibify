package net.chibify.chibify.mixin;

import net.chibify.chibify.mixininterface.IEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IEntityRenderState {
    @Unique
    private Entity chibify$entity;

    @Unique
    private boolean chibify$modelScaled;

    @Override
    public Entity chibify$getEntity() {
        return this.chibify$entity;
    }

    @Override
    public void chibify$setEntity(Entity entity) {
        this.chibify$entity = entity;
    }

    @Override
    public boolean chibify$isModelScaled() {
        return this.chibify$modelScaled;
    }

    @Override
    public void chibify$setModelScaled(boolean scaled) {
        this.chibify$modelScaled = scaled;
    }
}
