package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

public interface IElementReagent extends IReagent{
	
	public MagicUnit getAlignment();
	
	/**
	 * 0: Primary
	 * 1: Secondary
	 * 2: Tertiary
	 */
	public byte getLevel();

}
