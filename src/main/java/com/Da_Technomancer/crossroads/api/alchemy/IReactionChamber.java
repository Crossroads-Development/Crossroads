package com.Da_Technomancer.crossroads.api.alchemy;

import net.minecraft.core.particles.ParticleOptions;

import javax.annotation.Nonnull;

/**
 * To be placed on things that store alchemy reagents and allow reactions.
 */
public interface IReactionChamber{

	@Nonnull
	ReagentMap getReagents();
	
	boolean isCharged();
	
	int getReactionCapacity();
	
	/**
	 * Destroys the chamber, and creates an explosion
	 */
	void destroyChamber(float strength);
	
	/**
	 * Adds temporary particles for visual effect after a reaction. 
	 * @param particleType The particle type to spawn
	 * @param speedX Will be passed as offsetX
	 * @param speedY Will be passed as offsetY
	 * @param speedZ Will be passed as offsetZ
	 */
	<T extends ParticleOptions> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ);
}
