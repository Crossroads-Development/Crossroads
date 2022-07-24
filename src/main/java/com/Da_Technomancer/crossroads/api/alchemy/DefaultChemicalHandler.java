package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import net.minecraft.core.Direction;

public class DefaultChemicalHandler implements IChemicalHandler{

	@Override
	public EnumTransferMode getMode(Direction side){
		return EnumTransferMode.NONE;
	}

	@Override
	public EnumContainerType getChannel(Direction side){
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
	public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, boolean ignorePhase){
		return false;
	}
}
