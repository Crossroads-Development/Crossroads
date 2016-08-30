package com.Da_Technomancer.crossroads.API.magic;

import javax.annotation.Nullable;

/** This capability is to be placed on things that RECIEVE magic, not for things that send magic.*/
public interface IMagicHandler{
	
	public final int MAX_DISTANCE = 16;
	public final int BEAM_TIME = 5;
	
	public void recieveMagic(MagicUnit mag);
	
	/** Beam senders should check canPass of any magicHandlers the beam meets, 
	 * and if the result != null then pretend the tile entity doesn't exist and continue checking with the RETURNED Magic Unit. 
	 * If result == null, then run recieveMagic with the MagicUnit. NOT with null.
	 * 
	 */
	@Nullable
	public MagicUnit canPass(MagicUnit mag);

}
