package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;

import javax.annotation.Nonnull;

public interface IElementReagent extends IReagent{

	@Nonnull
	public EnumBeamAlignments getAlignment();

}
