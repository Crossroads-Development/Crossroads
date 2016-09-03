package com.Da_Technomancer.crossroads.API.magic;

import javax.annotation.Nullable;

/** This capability is to be placed on things that RECIEVE magic, not for things that send magic.*/
public interface IMagicHandler{
	
	public final int MAX_DISTANCE = 16;
	public final int BEAM_TIME = 5;
	
	/**
	 * This should be implemented based on a toggle system: handlers should assume that they receive the last magic unit sent with this method continuously
	 */
	public void setMagic(@Nullable MagicUnit mag);
}
