package com.Da_Technomancer.crossroads.API.rotary;

import javax.annotation.Nonnull;

/**
 * Gears and other rotary connectables use two capabilities, ICogHandler and IAxleHandler. 
 * The AxleHandler represents the core of the block, that can connect to machines and axles.
 * The CogHandler represents the part of the block able to connect to other blocks laterally, like the cogs of two gears meshing together.
 *
 * In most cases, the AxleHandler and CogHandler are on the same side, though there are exceptions. Some blocks may only have one of them.
 */
public interface IAxleHandler{

	/**
	 * [0]=w, [1]=E, [2]=P, [3]=lastE.
	 * Must be mutable and allow modification of the original values through it.
	 */
	double[] getMotionData();

	/**
	 * If lastRadius equals 0, then the AxleHandler should not convert the rotationRationIn, as this is an axial connection.
	 * The caller is normally responsible for adjusting the sign on the rotationRatioIn
	 * @param masterIn The originating Master Axis
	 * @param key The propogation key, used for determining if this block has already been checked
	 * @param rotationRatioIn The rotationRatio of the calling device (which means this block is responsible for adjusting sign and magnitude)
	 * @param lastRadius The radius of the previous connected device. 0 when connecting axially
	 * @param renderOffset Whether to render this block at an offset angle. This value should ONLY be used for rendering.
	 */
	void propogate(@Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, boolean renderOffset);

	double getMoInertia();
	
	double getRotationRatio();

	/**
	 * Adds energy to this axle
	 * @param energy The amount of energy to add
	 * @param absolute Whether the change is relative (positive energy increases speed, negative decreases), or absolute (positive energy could increase or decrease speed depending on spin direction)
	 */
	default void addEnergy(double energy, boolean absolute){
		double[] motionData = getMotionData();
		if(absolute){
			motionData[1] += energy;
		}else{
			int sign = (int) Math.signum(motionData[1]);
			motionData[1] += energy * ((double) sign);
			if(Math.signum(motionData[1]) != sign){//Don't allow decreasing energy to flip spin direction- stop at zero
				motionData[1] = 0;
			}
		}
		markChanged();
	}
	
	/**
	 * Should be called whenever a value in the AxleHandler is changed by something outside the AxleHandler. 
	 * Used to markDirty() in tile entities. 
	 */
	void markChanged();
	
	/**
	 * @return The angle of this axle for rendering. In degrees
	 */
	float getAngle(float partialTicks);
	
	/**
	 * @deprecated Due to improvements in how angles are synchronized and calculated, this method is no longer used
	 * @return Whether the Master Axis should keep the angle and clientW synchronized to client.
	 */
	@Deprecated
	default boolean shouldManageAngle(){
		return true;
	}

	/**
	 * Called by the controlling master axis when relinquishing control of this axle. Can be used along with propogate to determine if this axle is actively controlled by an axis
	 */
	default void disconnect(){

	}
}
