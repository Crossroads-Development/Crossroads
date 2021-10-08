package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class RenderEmpty extends EntityRenderer<Entity>{

	protected RenderEmpty(EntityRenderDispatcher renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity){
		return null;
	}
}
