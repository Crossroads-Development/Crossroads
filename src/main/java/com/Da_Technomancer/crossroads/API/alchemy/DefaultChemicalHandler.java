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
	public int getContent(){
		return 0;
	}

	@Override
	public int getContent(String type){
		return 0;
	}

	@Override
	public int getTransferCapacity(){
		return 0;
	}

	@Override
	public double getHeat(){
		return 0;
	}

	@Override
	public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
		return false;
	}

	@Override
	public void setHeat(double heat){
		
	}
}
