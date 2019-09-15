package com.Da_Technomancer.crossroads.API.rotary;

import net.minecraft.util.Direction;

public class DefaultCogHandler implements ICogHandler{

	@Override
	public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
		
	}

	@Override
	public IAxleHandler getAxle(){
		return null;
	}
}