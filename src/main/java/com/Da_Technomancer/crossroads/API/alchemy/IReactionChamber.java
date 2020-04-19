package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.particles.IParticleData;

import javax.annotation.Nonnull;

/**
 * To be placed on things that store alchemy reagents and allow reactions.  *
 */
public interface IReactionChamber{

	@Nonnull
	ReagentMap getReagants();
	
	/**
	 * @return This chamber's temperature in Degrees C
	 */
	default double getTemp(){
		return getReagants().getTempC();
	}
	
	default int getContent(){
		return getReagants().getTotalQty();
	}
	
	default boolean isCharged(){
		return false;
	}
	
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
	<T extends IParticleData> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ);
}
