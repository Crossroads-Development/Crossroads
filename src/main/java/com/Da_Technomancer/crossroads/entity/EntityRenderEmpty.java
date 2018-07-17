package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class EntityRenderEmpty extends Render<Entity>{

	protected EntityRenderEmpty(RenderManager renderManager){
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return null;
	}
}
