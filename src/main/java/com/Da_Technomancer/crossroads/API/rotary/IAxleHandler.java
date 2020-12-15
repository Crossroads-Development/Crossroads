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
	 * Should refer back to the master axis, or 0 if not actively controlled
	 * @return The current speed of this axle, in rad/s
	 */
	double getSpeed();

	/**
	 * Gets the energy of this gear. Negative values indicate negative spin direction
	 * @return The energy in joules
	 */
	double getEnergy();

	/**
	 * Set the energy content of this axle
	 * @param newEnergy The new energy value, in joules
	 */
	void setEnergy(double newEnergy);

	/**
	 * Adds energy to this axle
	 * @param energyChange The amount of energy to add
	 * @param absolute Whether the change is relative (positive energy increases speed, negative decreases, cannot flip spin direction), or absolute (positive energy could increase or decrease speed depending on spin direction)
	 */
	default void addEnergy(double energyChange, boolean absolute){
		double currEnergy = getEnergy();
		if(absolute){
			setEnergy(currEnergy + energyChange);
		}else{
			double sign = Math.signum(currEnergy);
			setEnergy(currEnergy + energyChange * sign);
			if(Math.signum(getEnergy()) != sign){//Don't allow decreasing energy to flip spin direction- stop at zero
				setEnergy(0);
			}
		}
	}

	double getMoInertia();

	double getRotationRatio();

	/**
	 * @return The angle of this axle for rendering. In degrees
	 */
	float getAngle(float partialTicks);

	/**
	 * If lastRadius equals 0, then the AxleHandler should not convert the rotationRationIn, as this is an axial connection.
	 * The caller is normally responsible for adjusting the sign on the rotationRatioIn
	 * @param masterIn The originating Master Axis
	 * @param key The propogation key, used for determining if this block has already been checked
	 * @param rotationRatioIn The rotationRatio of the calling device (which means this block is responsible for adjusting sign and magnitude)
	 * @param lastRadius The radius of the previous connected device. 0 when connecting axially
	 * @param renderOffset Whether to render this block at an offset angle. This value should ONLY be used for rendering.
	 */
	void propagate(@Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, boolean renderOffset);

	/**
	 * Called by the controlling master axis when relinquishing control of this axle. Can be used along with propogate to determine if this axle is actively controlled by an axis
	 */
	default void disconnect(){

	}
}
