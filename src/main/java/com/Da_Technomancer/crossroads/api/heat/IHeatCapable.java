package com.Da_Technomancer.crossroads.api.heat;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IHeatCapable{
	IBlockCapabilityProvider<IHeatHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IHeatCapable heatCapable){
			return heatCapable.getHeatHandler(side);
		}
		return null;
	};

	@Nullable
	IHeatHandler getHeatHandler(Direction dir);
}
