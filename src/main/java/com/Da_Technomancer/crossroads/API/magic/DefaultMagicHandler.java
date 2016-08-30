package com.Da_Technomancer.crossroads.API.magic;

import javax.annotation.Nullable;

public class DefaultMagicHandler implements IMagicHandler{

	@Override
	public void recieveMagic(MagicUnit mag){
		
	}
	
	@Nullable
	@Override
	public MagicUnit canPass(MagicUnit mag){
		return null;
	}
}
