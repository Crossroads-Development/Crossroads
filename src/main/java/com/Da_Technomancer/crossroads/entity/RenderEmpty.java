package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEmpty extends EntityRenderer<Entity>{

	protected RenderEmpty(EntityRendererManager renderManager){
		super(renderManager);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity){
		return null;
	}
}
