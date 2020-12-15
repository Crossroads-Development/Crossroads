package com.Da_Technomancer.crossroads.API.rotary;

public class DefaultAxleHandler implements IAxleHandler{

	@Override
	public double getSpeed(){
		return 0;
	}

	@Override
	public double getEnergy(){
		return 0;
	}

	@Override
	public void setEnergy(double newEnergy){

	}

	@Override
	public double getMoInertia(){
		return 0;
	}

	@Override
	public void propagate(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, boolean renderOffset){
		
	}

	@Override
	public double getRotationRatio(){
		return 0;
	}

	@Override
	public float getAngle(float partialTicks){
		return 0;
	}
}
