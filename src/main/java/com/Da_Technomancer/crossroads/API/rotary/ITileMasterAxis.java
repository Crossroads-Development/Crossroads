package com.Da_Technomancer.crossroads.API.rotary;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

public interface ITileMasterAxis{

	public void trigger(byte key, @Nonnull ITileMasterAxis masterIn, EnumFacing side);

	public void requestUpdate();

	public void lock();

	public boolean isLocked();

	/**
	 * returns true if master is locked
	 * 
	 */
	public boolean addToList(@Nonnull IAxleHandler handler);

	/** For debugging mainly */
	public double getTotalEnergy();
}
