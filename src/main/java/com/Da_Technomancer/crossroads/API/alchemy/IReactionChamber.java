package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IReactionChamber{
	
	@Nullable
	public IReagent getCatalyst();
	
	@Nullable
	public IReagent getSolid();
	
	@Nonnull
	public List<IReagent> getNonSolidReagants();
	
}
