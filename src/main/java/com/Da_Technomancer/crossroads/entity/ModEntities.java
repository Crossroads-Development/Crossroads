package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class ModEntities{
	
	public static void init(){
		int id = 1;
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "bullet"), EntityBullet.class, "bullet", id++, Main.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Main.MODID, "arm_ridable"), EntityArmRidable.class, "arm_ridable", id++, Main.instance, 64, 1, false);
	}
}
