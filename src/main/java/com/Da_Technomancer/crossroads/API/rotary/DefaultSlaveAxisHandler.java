package com.Da_Technomancer.crossroads.API.rotary;

import java.util.HashSet;

import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.util.EnumFacing;

public class DefaultSlaveAxisHandler implements ISlaveAxisHandler{

	@Override
	public void trigger(EnumFacing side){
		
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
