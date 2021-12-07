package com.Da_Technomancer.crossroads.ambient.particles;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.CreateParticlesOnClient;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
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
		ParticleEngine manager = Minecraft.getInstance().particleEngine;
		manager.register(COLOR_FLAME, ParticleFlameColor.Factory::new);
		manager.register(COLOR_GAS, ParticleBubbleColor.Factory::new);
		manager.register(COLOR_LIQUID, ParticleDripColor.Factory::new);
		manager.register(COLOR_SOLID, ParticlePowderColor.Factory::new);
		manager.register(COLOR_SPLASH, ParticleSplashColor.Factory::new);
	}

	/**
	 * Spawns particles for all nearby players
	 * Unlike the vanilla methods for spawning particles, the parameters in this actually do what they say
	 * Also, velocities can be distributed as desired, which enables additional functionality
	 * @param world The world to spawn them in
	 * @param data The particle data for the particles to be created
	 * @param count The number of particles to summon. Must be positive- 0 does nothing (and does not unlock additional functionality)
	 * @param x Median X position of the summoned particles
	 * @param y Median Y position of the summoned particles
	 * @param z Median Z position of the summoned particles
	 * @param xDeviation Random deviation in the X position of the summoned particles
	 * @param yDeviation Random deviation in the Y position of the summoned particles
	 * @param zDeviation Random deviation in the Z position of the summoned particles
	 * @param xVelocity Median X velocity of the summoned particles
	 * @param yVelocity Median Y velocity of the summoned particles
	 * @param zVelocity Median Z velocity of the summoned particles
	 * @param xVelocityDeviation Random deviation in the X velocity of the summoned particles
	 * @param yVelocityDeviation Random deviation in the Y velocity of the summoned particles
	 * @param zVelocityDeviation Random deviation in the Z velocity of the summoned particles
	 * @param gaussianDistribution If true, particle position and velocity will be randomized with a gaussian distribution about the median, with standard deviation of 'Deviation'. If false, a uniform distribution will be used, with maximum difference from the median of 'Deviation'
	 */
	public static void summonParticlesFromServer(ServerLevel world, ParticleOptions data, int count, double x, double y, double z, double xDeviation, double yDeviation, double zDeviation, double xVelocity, double yVelocity, double zVelocity, double xVelocityDeviation, double yVelocityDeviation, double zVelocityDeviation, boolean gaussianDistribution){
		CreateParticlesOnClient packet = new CreateParticlesOnClient(data, x, y, z, (float) xDeviation, (float) yDeviation, (float) zDeviation, (float) xVelocity, (float) yVelocity, (float) zVelocity, (float) xVelocityDeviation, (float) yVelocityDeviation, (float) zVelocityDeviation, count, gaussianDistribution);
		for(ServerPlayer target : world.players()){
			if(target.m_9236_() == world && target.blockPosition().closerThan(new Vec3(x, y, z), 32.0D)){
				CRPackets.sendPacketToPlayer(target, packet);
			}
		}
	}
}