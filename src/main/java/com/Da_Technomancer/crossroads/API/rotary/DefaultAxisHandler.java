package com.Da_Technomancer.crossroads.API.rotary;

public class DefaultAxisHandler implements IAxisHandler{

	@Override
	public void trigger(IAxisHandler masterIn, byte key){
		
	}

	@Override
	public void requestUpdate(){
		
	}

	@Override
	public void lock(){
		
	}

	@Override
	public boolean isLocked(){
		return false;
	}

	@Override
	public boolean addToList(IAxleHandler handler){
		return false;
	}

	@Override
	public double getTotalEnergy(){
		return 0;
	}

	@Override
	public float getAngle(double rotRatio, float partialTicks, boolean shouldOffset, float angleOffset){
		return 0;
	}

	@Override
	public AxisTypes getType(){
		return AxisTypes.NORMAL;
	}
}
