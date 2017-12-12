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
	public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
		return false;
	}

	@Override
	public void setHeat(double heat){
		
	}

	@Override
	public double getContent(int type){
		return 0;
	}
}
