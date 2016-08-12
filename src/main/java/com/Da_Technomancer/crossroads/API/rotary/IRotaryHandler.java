package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.enums.GearTypes;

/**
 * This is only to be used on things that can actually transfer rotary energy.
 * Not for things that interact with the rotary system externally (ex. pump)
 * The master axis does not carry rotary energy in itself.
 * 
 * Also, please note that some tile entities with this capability will return false 
 * to hasCapability if member == null, but will still return the handler with getCapability, for
 * setting the member. Not all tile entities with this capability act that way though
 */

public interface IRotaryHandler{

	
	/**	
	 * [0]=w, [1]=E, [2]=P, [3]=lastE
	 */
	public double[] getMotionData();
	
	public void propogate(int key, ITileMasterAxis masterIn);
	
	public void setMotionData(double[] dataIn);
	
	/**
	 * [0]=r, [1]=m, [2]=I
	 */
	public double[] getPhysData();
	
	public void setPhysData(double[] dataIn);
	
	public double keyType();
	
	public void resetAngle();
	
	public void setQ(double QIn, boolean client);
	
	public double getAngle();
	
	public void updateStates();
	
	/**negative value decreases energy. For non-gears (or axises) affecting the network
	 * absolute controls whether the change is relative or absolute (to spin direction)
	 */
	public void addEnergy(double energy, boolean allowInvert, boolean absolute);
	
	public void setMember(GearTypes membIn);
	
	public GearTypes getMember();
	
}
