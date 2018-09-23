package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;

public class DefaultCogHandler implements ICogHandler{

	@Override
	public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
		
	}

	@Override
	public IAxleHandler getAxle(){
		return null;
	}
}