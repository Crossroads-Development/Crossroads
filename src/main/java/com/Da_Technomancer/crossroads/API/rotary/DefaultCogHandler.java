package com.Da_Technomancer.crossroads.API.rotary;

import net.minecraft.util.EnumFacing;

public class DefaultCogHandler implements ICogHandler{

	@Override
	public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, EnumFacing cogOrient){
		
	}

	@Override
	public IAxleHandler getAxle(){
		return null;
	}
}