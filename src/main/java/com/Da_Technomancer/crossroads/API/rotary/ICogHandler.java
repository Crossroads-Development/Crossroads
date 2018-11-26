package com.Da_Technomancer.crossroads.API.rotary;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Gears and other rotary connectables use two capabilities, ICogHandler and IAxleHandler. 
 * The AxleHandler represents the core of the block, that can connect to machines and axles.
 * The CogHandler represents the part of the block able to connect to other blocks.
 *
 * In most cases, the AxleHandler and CogHandler are on the same side, though there are exceptions. Some blocks may only have one of them.
 */
public interface ICogHandler{
	
	/**
	 * Should redirect to the AxleHandler propogate method.
	 * @param cogOrient The orientation of the cogs in the plane (as opposed to the alignment of the plane, which is the capability side)
	 */
	public void connect(@Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, EnumFacing cogOrient);

	public IAxleHandler getAxle();
}
