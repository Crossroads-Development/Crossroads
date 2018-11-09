package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;

public class DefaultAxleHandler implements IAxleHandler{

	private double[] motionData = new double[4];
	private double physData = 0;

	@Override
	public double[] getMotionData(){
		return motionData;
	}

	@Override
	public double getMoInertia(){
		return physData;
	}

	@Override
	public void propogate(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
		
	}

	@Override
	public double getRotationRatio(){
		return 0;
	}
	
	@Override
	public void markChanged(){
		
	}

	@Override
	public boolean shouldManageAngle(){
		return false;
	}
}
