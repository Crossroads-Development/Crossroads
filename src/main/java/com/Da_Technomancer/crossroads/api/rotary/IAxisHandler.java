package com.Da_Technomancer.crossroads.api.rotary;

import javax.annotation.Nonnull;

public interface IAxisHandler{

	//Rotary state getters

	/**
	 * Gets the total energy of the system, normalized for direction
	 * @return Total energy of the system, in Joules. Negative value indicates negative spin on the reference gear
	 */
	double getTotalEnergy();

	/**
	 * Gets the change in total energy this tick
	 * @return Change in total energy this tick, in Joules
	 */
	double getEnergyChange();

	/**
	 * Change in total energy this tick due to energy loss
	 * @return Change in total energy this tick due to energy loss, in Joules. Should be opposite sign of getTotalEnergy()
	 */
	double getEnergyLost();

	/**
	 * Gets the speed on the base gear. Any other gear in this system has speed=(base speed) * (rotation ratio)
	 * @return The speed on the base gear, in rad/s
	 */
	double getBaseSpeed();

	/**
	 * During propagation, gears should trigger all found axes. Depending on conditions, this might lock the axis.
	 * @param masterIn The axis the gear is propagating from.
	 * @param key The key the gear is using. 
	 */
	void trigger(@Nonnull IAxisHandler masterIn, byte key);
	
	void requestUpdate();

	void lock();

	boolean isLocked();

	/**
	 * @return true if master is locked
	 */
	boolean addToList(@Nonnull IAxleHandler handler);

	/**
	 * Gets the render angle for member axles, in degrees
	 * @param rotRatio The axle rotation ratios
	 * @param partialTicks Rendering partial ticks
	 * @param shouldOffset Whether this gear has a visually offset angle
	 * @param angleOffset The offset this gear would use if shouldOffset is true, in degrees
	 * @return The angle for use in rendering
	 */
	float getAngle(double rotRatio, float partialTicks, boolean shouldOffset, float angleOffset);

	/**
	 * Get the overarching category this axis fits into. Fixed means strict external control speed/energy, normal means normal
	 * @return The category this axis is.
	 */
	@Nonnull
	AxisTypes getType();
}
