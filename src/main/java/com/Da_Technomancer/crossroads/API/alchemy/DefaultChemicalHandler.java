package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
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
	public int getContent(IReagent type){
		return 0;
	}

	@Override
	public int getTransferCapacity(){
		return 0;
	}

	@Override
	public double getTemp(){
		return HeatUtil.ABSOLUTE_ZERO;
	}

	@Override
	public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
		return false;
	}
}
