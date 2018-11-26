package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import net.minecraft.util.EnumFacing;

public class DefaultAxisHandler implements IAxisHandler{

	@Override
	public void trigger(IAxisHandler masterIn, byte key){
		
	}

	@Override
	public void requestUpdate(){
		
	}

	@Override
	public void lock(){
		
	}

	@Override
	public boolean isLocked(){
		return false;
	}

	@Override
	public boolean addToList(IAxleHandler handler){
		return false;
	}

	@Override
	public void addAxisToList(ISlaveAxisHandler handler, EnumFacing side){
		
	}

	@Override
	public double getTotalEnergy(){
		return 0;
	}
}
