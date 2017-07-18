package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.ModConfig;

import net.minecraft.util.EnumFacing;

public class DefaultAxisHandler implements IAxisHandler{

	/**
	 * This method should be called BEFORE adding an ISlaveAxisHandler to the stored list.
	 * 
	 * @param axis An ISlaveAxisHandler of the IAxisHandler calling this method.
	 * @param toAdd The ISlaveAxisHandler found during propagation
	 * @return true if toAdd contains axis, even if nested. If true, the calling IAxisHandler should self-destruct (or otherwise suspend operation).
	 * <p>
	 * It is possible for this method to throw a StackOverflow error. There are two possible causes of this: either there is an unreasonable amount of nesting going on,
	 * or there is a different infinite loop that should have been prevented at an earlier point. The disableSlaves config can be used to rescue a world in either of these cases.
	 */
	public static boolean contains(ISlaveAxisHandler axis, ISlaveAxisHandler toAdd){
		if(ModConfig.disableSlaves.getBoolean() || toAdd == axis){
			return true;
		}
		if(toAdd.getContainedAxes().isEmpty()){
			return false;
		}
		for(ISlaveAxisHandler inner : toAdd.getContainedAxes()){
			if(contains(axis, inner)){
				return true;
			}
		}
		return false;
	}
	
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
