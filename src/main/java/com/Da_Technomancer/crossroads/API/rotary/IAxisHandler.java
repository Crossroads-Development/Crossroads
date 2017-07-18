package com.Da_Technomancer.crossroads.API.rotary;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

public interface IAxisHandler{

	/**
	 * During propagation, gears should trigger all found axes. Depending on conditions, this should lock the axis.
	 * @param masterIn The axis the gear is propagating from.
	 * @param key The key the gear is using. 
	 */
	public void trigger(@Nonnull IAxisHandler masterIn, byte key);
	
	public void requestUpdate();

	public void lock();

	public boolean isLocked();

	/**
	 * @return true if master is locked
	 */
	public boolean addToList(@Nonnull IAxleHandler handler);
	
	/**
	 * During propagation, gears should call this in the propagating axis for any {@link ISlaveAxisHandler} found. 
	 * The side is the side the gear found, and it should be used for calling {@link ISlaveAxisHandler#trigger(EnumFacing)} <p>
	 * If the one given that belongs to either this axis, or any axis that controls this axis' ISlaveAxisHandler through any amount of nesting, this axis should self-destruct somehow. 
	 * It is recommended to use {@link DefaultAxisHandler#contains(ISlaveAxisHandler, ISlaveAxisHandler)} to find a loop. 
	 */
	public void addAxisToList(@Nonnull ISlaveAxisHandler handler, EnumFacing side);

	public double getTotalEnergy();
}
