package net.chibify.chibify.mixininterface;


import net.minecraft.entity.Entity;

public interface IEntityRenderState {
    Entity chibify$getEntity();

    void chibify$setEntity(Entity entity);
}