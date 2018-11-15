package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;

public interface IElementReagent extends IReagent{
	
	public BeamUnit getAlignment();
	
	/**
	 * 0: Primary
	 * 1: Secondary
	 * 2: Tertiary
	 */
	public byte getLevel();
	
	@Nullable
	public IElementReagent getSecondaryBase();

}
