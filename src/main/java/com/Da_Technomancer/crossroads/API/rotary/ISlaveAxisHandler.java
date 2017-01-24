package com.Da_Technomancer.crossroads.API.rotary;

import java.util.HashSet;

import net.minecraft.util.EnumFacing;
/**
 * 
 * A slave axis is one which is incapable of running calculations and adjusting gears without first being triggered by one or more other axes. 
 * This capability is for sides with other gear networks can connect to this axis on for triggering (and determining input values). {@link IAxisHandler} should still
 * be used on the active side. 
 */
public interface ISlaveAxisHandler{

	/**
	 * Yes, the side parameter is intentional and should be supplied even though this is a capability. 
	 * @param side The side of the axis triggered. 
	 */
	public void trigger(EnumFacing side);
	
	/**
	 * @return Any ISlaveAxisHandlers controlled by this axis. 
	 */
	public HashSet<ISlaveAxisHandler> getContainedAxes();
	
	/** For debugging mainly */
	public double getTotalEnergy();
}
