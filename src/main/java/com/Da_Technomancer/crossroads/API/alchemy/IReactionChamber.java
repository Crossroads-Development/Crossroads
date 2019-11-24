package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.particles.IParticleData;

import javax.annotation.Nonnull;

/**
 * To be placed on things that store alchemy reagents and allow reactions.  *
 */
public interface IReactionChamber{

	@Nonnull
	public ReagentMap getReagants();
	
//	/**
//	 * Not the same as temperature.
//	 * @return
//	 */
//	public double getHeat();
//
//	public void setHeat(double heatIn);
//
//	public default void addHeat(double heatChange){
//		setHeat(getHeat() + heatChange);
//	}
	
	/**
	 * @return This chamber's temperature in Degrees C
	 */
	public default double getTemp(){
		return getReagants().getTempC();
	}
	
	public default int getContent(){
		return getReagants().getTotalQty();
	}
	
	public default boolean isCharged(){
		return getReagants().getQty(EnumReagents.ELEM_CHARGE.id()) != 0;
	}
	
	public int getReactionCapacity();
	
	/**
	 * Destroys the chamber, and creates an explosion
	 */
	public void destroyChamber(float strength);
	
	
	/**
	 * Adds temporary particles for visual effect after a reaction. 
	 * @param particleType
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public <T extends IParticleData> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ);
}
