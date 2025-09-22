package com.Da_Technomancer.crossroads.api.beams;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IBeamCapable{
	IBlockCapabilityProvider<IBeamHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IBeamCapable beamCapable){
			return beamCapable.getBeamHandler(side);
		}
		return null;
	};

	@Nullable
	IBeamHandler getBeamHandler(Direction dir);
}
