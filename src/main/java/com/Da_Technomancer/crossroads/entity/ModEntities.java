package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class ModEntities{

	public static void init(){
		int id = 1;
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "bullet"), EntityBullet.class, "bullet", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "arm_ridable"), EntityArmRidable.class, "arm_ridable", id++, Main.instance, 64, 1, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "shell"), EntityShell.class, "shell", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "nitro"), EntityShell.class, "nitro", id++, Main.instance, 64, 5, true);
	}

	public static void clientInit(){
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (RenderManager manager) -> (new RenderSnowball<EntityBullet>(manager, Items.IRON_NUGGET, Minecraft.getMinecraft().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityShell.class, (RenderManager manager) -> (new RenderSnowball<EntityShell>(manager, ModItems.shell, Minecraft.getMinecraft().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityNitro.class, (RenderManager manager) -> (new RenderSnowball<EntityNitro>(manager, ModItems.nitroglycerin, Minecraft.getMinecraft().getRenderItem())));
	}
}
