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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModEntities{

	public static void init(){
		int id = 1;
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "bullet"), EntityBullet.class, "bullet", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "arm_ridable"), EntityArmRidable.class, "arm_ridable", id++, Main.instance, 64, 1, false);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "shell"), EntityShell.class, "shell", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "nitro"), EntityNitro.class, "nitro", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "flying_machine"), EntityFlyingMachine.class, "flying_machine", id++, Main.instance, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "flame_core"), EntityFlameCore.class, "flame_core", id++, Main.instance, 64, 20, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "ghost_marker"), EntityGhostMarker.class, "ghost_marker", id++, Main.instance, 64, 20, true);
	}

	@SideOnly(Side.CLIENT)
	public static void clientInit(){
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, (RenderManager manager) -> (new RenderSnowball<EntityBullet>(manager, Items.IRON_NUGGET, Minecraft.getMinecraft().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityArmRidable.class, (RenderManager manager) -> (new EntityRenderEmpty(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityShell.class, (RenderManager manager) -> (new RenderSnowball<EntityShell>(manager, ModItems.shell, Minecraft.getMinecraft().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityNitro.class, (RenderManager manager) -> (new RenderSnowball<EntityNitro>(manager, ModItems.nitroglycerin, Minecraft.getMinecraft().getRenderItem())));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingMachine.class, (RenderManager manager) -> (new RenderFlyingMachine(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityFlameCore.class, (RenderManager manager) -> (new RenderFlameCoreEntity(manager)));
		RenderingRegistry.registerEntityRenderingHandler(EntityGhostMarker.class, (RenderManager manager) -> (new EntityRenderEmpty(manager)));
	}
}
