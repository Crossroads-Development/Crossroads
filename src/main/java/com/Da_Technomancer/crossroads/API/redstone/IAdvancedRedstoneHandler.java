package com.Da_Technomancer.crossroads.API.redstone;

public interface IAdvancedRedstoneHandler{
	
	/**
	 * @param measure An implementation can require this to be true, otherwise returning 0. This being true represents being read by something like the back face of a ratiator (which functions like a comparator).
	 * @return The output. MUST BE >= 0.
	 */
	public double getOutput(boolean measure);

}
