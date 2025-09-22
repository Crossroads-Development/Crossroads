package com.Da_Technomancer.crossroads.api.alchemy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IChemicalCapable{
	IBlockCapabilityProvider<IChemicalHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IChemicalCapable chemicalCapable){
			return chemicalCapable.getChemicalHandler(side);
		}
		return null;
	};

	@Nullable
	IChemicalHandler getChemicalHandler(Direction dir);
}
