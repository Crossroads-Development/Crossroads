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
		ParticleManager manager = Minecraft.getInstance().particles;
		manager.registerFactory(COLOR_FLAME, ParticleFlameColor.Factory::new);
		manager.registerFactory(COLOR_GAS, ParticleBubbleColor.Factory::new);
		manager.registerFactory(COLOR_LIQUID, ParticleDripColor.Factory::new);
		manager.registerFactory(COLOR_SOLID, ParticlePowderColor.Factory::new);
		manager.registerFactory(COLOR_SPLASH, ParticleSplashColor.Factory::new);
	}
}