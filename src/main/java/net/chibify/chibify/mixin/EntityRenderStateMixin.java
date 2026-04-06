package net.chibify.chibify.mixin;

import net.chibify.chibify.mixininterface.IEntityRenderState;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements IEntityRenderState {
    @Unique
    private Entity entity;

    @Override
    public Entity chibify$getEntity() {
        return entity;
    }

    @Override
    public void chibify$setEntity(Entity entity) {
        this.entity = entity;
    }
}