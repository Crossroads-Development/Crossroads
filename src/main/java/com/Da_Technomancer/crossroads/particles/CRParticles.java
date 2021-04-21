package com.Da_Technomancer.crossroads.particles;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class CRParticles{

	@ObjectHolder("color_flame")
	public static ColorParticleType COLOR_FLAME = null;
	@ObjectHolder("color_gas")
	public static ColorParticleType COLOR_GAS = null;
	@ObjectHolder("color_liquid")
	public static ColorParticleType COLOR_LIQUID = null;
	@ObjectHolder("color_solid")
	public static ColorParticleType COLOR_SOLID = null;
	@ObjectHolder("color_splash")
	public static ColorParticleType COLOR_SPLASH = null;

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
		ParticleManager manager = Minecraft.getInstance().particleEngine;
		manager.register(COLOR_FLAME, ParticleFlameColor.Factory::new);
		manager.register(COLOR_GAS, ParticleBubbleColor.Factory::new);
		manager.register(COLOR_LIQUID, ParticleDripColor.Factory::new);
		manager.register(COLOR_SOLID, ParticlePowderColor.Factory::new);
		manager.register(COLOR_SPLASH, ParticleSplashColor.Factory::new);
	}
}