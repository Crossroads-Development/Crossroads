package com.Da_Technomancer.crossroads.api.electric;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyCapable{
	IBlockCapabilityProvider<IEnergyStorage, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IEnergyCapable energyCapable){
			return energyCapable.getEnergyHandler(side);
		}
		return null;
	};

	@Nullable
	IEnergyStorage getEnergyHandler(Direction dir);
}
