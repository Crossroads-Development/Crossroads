package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModEntities{

	public static void init(){
		int id = 1;
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "bullet"), EntityBullet.class, "bullet", id++, Crossroads.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "arm_ridable"), EntityArmRidable.class, "arm_ridable", id++, Crossroads.instance, 64, 1, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "shell"), EntityShell.class, "shell", id++, Crossroads.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "nitro"), EntityNitro.class, "nitro", id++, Crossroads.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "flying_machine"), EntityFlyingMachine.class, "flying_machine", id++, Crossroads.instance, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "flame_core"), EntityFlameCore.class, "flame_core", id++, Crossroads.instance, 64, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Crossroads.MODID, "ghost_marker"), EntityGhostMarker.class, "ghost_marker", id++, Crossroads.instance, 64, 20, true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (EntityRendererManager manager) -> (new SpriteRenderer<EntityBullet>(manager, Items.IRON_NUGGET, Minecraft.getInstance().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityArmRidable.class, (EntityRendererManager manager) -> (new EntityRenderEmpty(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityShell.class, (EntityRendererManager manager) -> (new SpriteRenderer<EntityShell>(manager, CrossroadsItems.shellGlass, Minecraft.getInstance().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityNitro.class, (EntityRendererManager manager) -> (new SpriteRenderer<EntityNitro>(manager, CrossroadsItems.nitroglycerin, Minecraft.getInstance().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingMachine.class, (EntityRendererManager manager) -> (new RenderFlyingMachine(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlameCore.class, (EntityRendererManager manager) -> (new RenderFlameCoreEntity(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityGhostMarker.class, (EntityRendererManager manager) -> (new EntityRenderEmpty(manager)));
	}
}
