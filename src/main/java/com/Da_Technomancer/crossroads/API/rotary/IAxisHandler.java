package com.Da_Technomancer.crossroads.API.rotary;

import javax.annotation.Nonnull;

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

	public double getTotalEnergy();

	/**
	 * Gets the render angle for member axles, in degrees
	 * @param rotRatio The axle rotation ratios
	 * @param partialTicks Rendering partial ticks
	 * @param shouldOffset Whether this gear has a visually offset angle
	 * @param angleOffset The offset this gear would use if shouldOffset is true, in degrees
	 * @return The angle for use in rendering
	 */
	public float getAngle(double rotRatio, float partialTicks, boolean shouldOffset, float angleOffset);

	/**
	 * Get the overarching category this axis fits into. Fixed means strict external control speed/energy, normal means normal
	 * @return The category this axis is.
	 */
	@Nonnull
	public AxisTypes getType();
}
