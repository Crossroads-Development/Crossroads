package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public interface IAlchemyContainer{
	
	public boolean isGlass();
	
	/**
	 * Note: might be called several times in quick succession. 
	 */
	public void destroyChamber();
	
	/**
	 * Array MUST be of size {@link AlchemyCraftingManager#RESERVED_REAGENT_COUNT} + {@link AlchemyCraftingManager#DYNAMIC_REAGENT_COUNT}. May contain null elements. 
	 * @return An array of the contained reagents. 
	 */
	@Nonnull
	public Reagent[] getReagants();
	
	public double getCapacity();
	
	public default double getContent(){
		double amount = 0;
		for(Reagent reag : getReagants()){
			if(reag != null && !reag.getPhase().compressable()){
				amount += reag.getAmount();
			}
		}
		return amount;
	}
}
