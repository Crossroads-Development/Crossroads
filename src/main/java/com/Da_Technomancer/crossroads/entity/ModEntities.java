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
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer(), 1)));
//		RenderingRegistry.registerEntityRenderingHandler(EntityArmRidable.class, RenderEmpty::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityShell.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer(), 1)));
		RenderingRegistry.registerEntityRenderingHandler(EntityNitro.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer(), 1)));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingMachine.class, RenderFlyingMachine::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlameCore.class, RenderFlameCoreEntity::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGhostMarker.class, RenderEmpty::new);
	}
}
