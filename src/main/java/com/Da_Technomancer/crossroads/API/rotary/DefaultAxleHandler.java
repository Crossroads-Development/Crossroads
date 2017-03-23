package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.MiscOp;

public class DefaultAxleHandler implements IAxleHandler{

	private double[] motionData = new double[4];
	private double[] physData = new double[2];

	@Override
	public double[] getMotionData(){
		return motionData;
	}

	@Override
	public double[] getPhysData(){
		return physData;
	}

	@Override
	public void resetAngle(){
		
	}

	@Override
	public double getAngle(){
		return 0;
	}

	@Override
	public void addEnergy(double energy, boolean allowInvert, boolean absolute){
		if(allowInvert && absolute){
			motionData[1] += energy;
		}else if(allowInvert){
			motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
		}else if(absolute){
			int sign = (int) MiscOp.posOrNeg(motionData[1]);
			motionData[1] += energy;
			if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}else{
			int sign = (int) MiscOp.posOrNeg(motionData[1]);
			motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
			if(MiscOp.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}
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
}
