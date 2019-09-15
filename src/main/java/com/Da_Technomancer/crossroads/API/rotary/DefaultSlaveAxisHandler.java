package com.Da_Technomancer.crossroads.API.rotary;

import java.util.HashSet;

import net.minecraft.util.Direction;

public class DefaultSlaveAxisHandler implements ISlaveAxisHandler{

	@Override
	public void trigger(Direction side){
		
	}

	@Override
	public HashSet<ISlaveAxisHandler> getContainedAxes(){
		return null;
	}

	@Override
	public boolean isInvalid(){
		return true;
	}
}
