package com.Da_Technomancer.crossroads.API.packets;

public interface ITaylorReceiver{

	/**
	 * @param timestamp The world time this series is defined relative to.
	 * @param series The coefficients of the Taylor series, including the factorial dividers. The item at index 0 is the 0 order term
	 */
	void receiveSeries(long timestamp, float[] series);

}
