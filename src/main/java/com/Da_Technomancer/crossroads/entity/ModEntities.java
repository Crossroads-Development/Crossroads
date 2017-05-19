package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModEntities{
	
	public static void init(){
		int id = 1;
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "bullet"), EntityBullet.class, "bullet", id++, Main.instance, 64, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "arm_ridable"), EntityArmRidable.class, "arm_ridable", id++, Main.instance, 0, 0, false);
	}

	@SideOnly(Side.CLIENT)
	public static void clientInit(){
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (RenderManager manager) -> (new RenderSnowball<EntityBullet>(manager, Items.field_191525_da, Minecraft.getMinecraft().getRenderItem())));
	}
}
