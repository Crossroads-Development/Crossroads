package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.EnumFacing;

public class DefaultChemicalHandler implements IChemicalHandler{

	@Override
	public boolean isGlass(){
		return false;
	}

	@Override
	public Reagent[] getReagants(){
		return new Reagent[AlchemyCraftingManager.RESERVED_REAGENT_COUNT + AlchemyCraftingManager.DYNAMIC_REAGENT_COUNT];
	}

	@Override
	public double getCapacity(){
		return 0;
	}

	@Override
	public EnumTransferMode getMode(EnumFacing side){
		return EnumTransferMode.NONE;
	}

	@Override
	public EnumContainerType getChannel(EnumFacing side){
		return EnumContainerType.NONE;
	}

	@Override
	public void markChanged(){
		
	}
}
