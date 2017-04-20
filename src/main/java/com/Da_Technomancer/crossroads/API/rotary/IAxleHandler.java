package com.Da_Technomancer.crossroads.API.rotary;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gears and other rotary connectables use two capabilities, ICogHandler and IAxleHandler. 
 * The AxleHandler represents the core of the block, that can connect to machines and axles.
 * The CogHandler represents the part of the block able to connect to other blocks.
 *
 * In most cases, the AxleHandler and CogHandler are on the same side, though there are exceptions. Some blocks may only have one of them.
 */
public interface IAxleHandler{

	/**
	 * [0]=w, [1]=E, [2]=P, [3]=lastE
	 */
	public double[] getMotionData();

	/**
	 * If lastRadius equals 0, then the AxleHandler should not convert the rotationRationIn based on radius (but may based on direction).
	 * If lastRadius and rotationRatioIn both equal 0, this should set it's internal rotationRatio to 1.
	 */
	public void propogate(@Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius);

	/**
	 * [0]=m, [1]=I
	 */
	public double[] getPhysData();
	
	public double getRotationRatio();

	public void resetAngle();

	/**
	 * To be used for rendering.
	 */
	@SideOnly(Side.CLIENT)
	public double getAngle();
	
	/**
	 * To be used for rendering with partial ticks. Use of this is completely optional. If it is used, then it should return a prediction for the next angle value (usually found by assuming the speed is constant).
	 */
	@SideOnly(Side.CLIENT)
	public default double getNextAngle(){
		return getAngle();
	}

	/**
	 * negative value decreases energy. For non-gears (or axises) affecting the
	 * network absolute controls whether the change is relative or absolute (to
	 * spin direction)
	 */
	public void addEnergy(double energy, boolean allowInvert, boolean absolute);
	
	/**
	 * Should be called whenever a value in the AxleHandler is changed by something outside the AxleHandler. 
	 * Used to markDirty() in tile entities. 
	 */
	public void markChanged();
}
