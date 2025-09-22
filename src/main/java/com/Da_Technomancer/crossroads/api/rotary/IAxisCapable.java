package com.Da_Technomancer.crossroads.api.rotary;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IAxisCapable{
	IBlockCapabilityProvider<IAxisHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IAxisCapable axisCapable){
			return axisCapable.getAxisHandler(side);
		}
		return null;
	};

	@Nullable
	IAxisHandler getAxisHandler(Direction dir);
}
