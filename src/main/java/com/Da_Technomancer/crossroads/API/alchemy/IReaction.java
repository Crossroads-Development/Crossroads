package com.Da_Technomancer.crossroads.API.alchemy;

public interface IReaction{

	/**
	 * Performs this reaction, if applicable. 
	 * @param chamber The chamber to check. 
	 * @return The change in °C*amount from this reaction (negative if increasing temperature, positive if decreasing it), or 0 if no reaction occured. All reactions MUST change temperature when performed! 
	 */
	public double performReaction(IReactionChamber chamber);

}
