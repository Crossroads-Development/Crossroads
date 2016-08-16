package com.Da_Technomancer.crossroads.API.rotary;

import net.minecraft.util.EnumFacing;

public interface ITileMasterAxis{
	
	public void trigger(int key, ITileMasterAxis masterIn, EnumFacing side);
	
	public void requestUpdate();
	
	public void lock();
	
	public boolean isLocked();
	
	/**returns true if master is locked
	 * 
	 */
	public boolean addToList(IRotaryHandler handler);
	
	/** For debugging mainly */
	public double getTotalEnergy();

}
