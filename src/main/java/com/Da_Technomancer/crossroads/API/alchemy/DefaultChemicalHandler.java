package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.EnumFacing;

public class DefaultChemicalHandler implements IChemicalHandler{

	@Override
	public EnumTransferMode getMode(EnumFacing side){
		return EnumTransferMode.NONE;
	}

	@Override
	public EnumContainerType getChannel(EnumFacing side){
		return EnumContainerType.NONE;
	}

	@Override
	public double getContent(){
		return 0;
	}

	@Override
	public double getTransferCapacity(){
		return 0;
	}

	@Override
	public double getHeat(){
		return 0;
	}

	@Override
	public boolean insertReagents(Reagent[] reag, EnumFacing side, IChemicalHandler caller){
		return false;
	}

	@Override
	public void setHeat(double heat){
		
	}
}
