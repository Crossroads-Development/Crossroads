package com.Da_Technomancer.crossroads.api.rotary;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IAxleCapable{
	IBlockCapabilityProvider<IAxleHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IAxleCapable axleCapable){
			return axleCapable.getAxleHandler(side);
		}
		return null;
	};

	@Nullable
	IAxleHandler getAxleHandler(Direction dir);
}
