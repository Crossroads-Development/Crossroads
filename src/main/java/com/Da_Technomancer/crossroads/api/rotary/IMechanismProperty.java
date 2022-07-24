package com.Da_Technomancer.crossroads.api.rotary;

public interface IMechanismProperty{

	/**
	 * For networking only! Not for saving/loading to disk
	 * @return An int that identifies this material, and can be deserialized by the mechanism
	 */
	int serialize();

	String getSaveName();
}
