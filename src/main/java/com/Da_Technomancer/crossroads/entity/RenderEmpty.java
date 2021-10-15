package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderEmpty extends EntityRenderer<Entity>{

	protected RenderEmpty(EntityRendererProvider.Context context){
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity){
		return null;
	}
}
