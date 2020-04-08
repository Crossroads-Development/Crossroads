package com.Da_Technomancer.crossroads.API.rotary;

public class DefaultAxleHandler implements IAxleHandler{

	private double[] motionData = new double[4];

	@Override
	public double[] getMotionData(){
		return motionData;
	}

	@Override
	public double getMoInertia(){
		return 0;
	}

	@Override
	public void propogate(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, boolean renderOffset){
		
	}

	@Override
	public double getRotationRatio(){
		return 0;
	}
	
	@Override
	public void markChanged(){
		
	}

	@Override
	public float getAngle(float partialTicks){
		return 0;
	}
}
