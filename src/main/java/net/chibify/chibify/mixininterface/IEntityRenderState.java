package net.chibify.chibify.mixininterface;

import net.minecraft.world.entity.Entity;

public interface IEntityRenderState {
    Entity chibify$getEntity();
    void chibify$setEntity(Entity entity);

    boolean chibify$isModelScaled();
    void chibify$setModelScaled(boolean scaled);
}
