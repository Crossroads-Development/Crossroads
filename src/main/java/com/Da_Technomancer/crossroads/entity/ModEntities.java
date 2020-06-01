package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public final class ModEntities{

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
//		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer())));
//		RenderingRegistry.registerEntityRenderingHandler(EntityArmRidable.class, RenderEmpty::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShell.type, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer())));
		RenderingRegistry.registerEntityRenderingHandler(EntityNitro.type, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer())));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingMachine.type, RenderFlyingMachine::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlameCore.type, RenderFlameCoreEntity::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGhostMarker.type, RenderEmpty::new);
	}
}
