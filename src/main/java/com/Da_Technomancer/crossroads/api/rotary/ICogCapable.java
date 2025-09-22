package com.Da_Technomancer.crossroads.api.rotary;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface ICogCapable{
	IBlockCapabilityProvider<ICogHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof ICogCapable cogCapable){
			return cogCapable.getCogHandler(side);
		}
		return null;
	};

	@Nullable
	ICogHandler getCogHandler(Direction dir);
}
