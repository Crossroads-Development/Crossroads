package com.Da_Technomancer.crossroads.API.magic;

import javax.annotation.Nullable;

/** This capability is to be placed on things that RECEIVE magic, not for things that send magic.*/
public interface IMagicHandler{
	
	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 5;
	
	/**
	 * Handlers should assume that they receive the last magic unit sent with this method continuously for the next BEAM_TIME ticks.
	 */
	public void setMagic(@Nullable MagicUnit mag);
}
