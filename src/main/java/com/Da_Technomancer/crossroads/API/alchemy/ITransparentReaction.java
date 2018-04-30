package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Implementers should use the performReaction method to actually perform the reactions. The other methods are used for JEI integration. 
 * Only default reactions should be transparent. 
 */
public interface ITransparentReaction extends IReaction{

	@Nullable
	public IReagent getCatalyst();
	
	/**
	 * In degrees C. Should account for reagent phase requirements. 
	 */
	public double minTemp();
	
	/**
	 * In degrees C. Should account for reagent phase requirements. 
	 */
	public double maxTemp();
	
	public boolean charged();
	
	/**
	 * @return The change in heat per reaction (with one reaction being the number of parts of each reagent. Partial reactions still occur). Negative increases heat. 
	 */
	public double deltaHeatPer();
	
	/**
	 * Each pair should consist of a required reagent type and the number of 'parts' required to perform the reaction. The number of parts is NOT the minimum quantity, and is only for specifying a ratio. 
	 */
	public Pair<IReagent, Integer>[] getReagents();
	
	/**
	 * Each pair should consist of a produced reagent type and the number of 'parts' created by the reaction. 
	 */
	public Pair<IReagent, Integer>[] getProducts();

}
