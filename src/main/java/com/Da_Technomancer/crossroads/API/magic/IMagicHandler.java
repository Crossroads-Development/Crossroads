package com.Da_Technomancer.crossroads.API.magic;

/** This capability is to be placed on things that RECIEVE magic, not for things that send magic.*/
public interface IMagicHandler{
	
	public final int MAX_DISTANCE = 16;
	
	public void recieveMagic(MagicUnit mag);

}
