package com.Da_Technomancer.crossroads.API.alchemy;

public interface IReaction{

	/**
	 * Performs this reaction, if applicable. 
	 * @param chamber The chamber to check.
	 * @return Whether this performed a reaction. 
	 */
	public boolean performReaction(IReactionChamber chamber);

}
