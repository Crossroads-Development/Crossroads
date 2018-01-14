package com.Da_Technomancer.crossroads.API.alchemy;

public interface IReaction{

	/**
	 * Performs this reaction, if applicable. 
	 * @param chamber The chamber to check. 
	 * @param solvents Size is EnumSolventType length. Each index specifies whether that solvent is present. Do not modify the passed array. 
	 * @return Whether this performed a reaction. 
	 */
	public boolean performReaction(IReactionChamber chamber, boolean[] solvents);

}
