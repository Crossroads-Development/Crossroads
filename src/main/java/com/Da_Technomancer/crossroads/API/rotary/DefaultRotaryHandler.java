package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.MiscOperators;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;

public class DefaultRotaryHandler implements IRotaryHandler{

	private double[] motionData = new double[4];
	private double[] physData = new double[3];
	private double angle = 0;
	private int key;
	private GearTypes member;

	@Override
	public double[] getMotionData(){
		return motionData;
	}

	@Override
	public void propogate(int key, ITileMasterAxis masterIn){
		this.key = key;

	}

	@Override
	public void setMotionData(double[] dataIn){
		motionData = dataIn;
	}

	@Override
	public double[] getPhysData(){
		return physData;
	}

	@Override
	public void setPhysData(double[] dataIn){
		physData = dataIn;
	}

	@Override
	public double keyType(){
		return MiscOperators.posOrNeg(key);
	}

	@Override
	public void resetAngle(){
		angle = 0;
	}

	@Override
	public void setQ(double QIn, boolean client){

	}

	@Override
	public double getAngle(){
		return angle;
	}

	@Override
	public void updateStates(){
		// assume each gear is 1/8 of a cubic meter and has a radius of 1/2
		// meter.
		// mass is rounded to make things nicer for everyone

		if(member == null){
			physData[0] = 0;
			physData[1] = 0;
			physData[2] = 0;
			motionData[0] = 0;
			motionData[1] = 0;
			motionData[2] = 0;
			motionData[3] = 0;
		}else{
			physData[1] = Math.round((member.getDensity() / 8) * 100D) / 100D;
			physData[0] = .5;
			physData[2] = physData[1] * .125; /*
												 * .125 because r*r/2 so .5*.5/2
												 */
		}
	}

	@Override
	public void addEnergy(double energy, boolean allowInvert, boolean absolute){

		if(allowInvert && absolute){
			motionData[1] += energy;
		}else if(allowInvert){
			motionData[1] += energy * MiscOperators.posOrNeg(motionData[1]);
		}else if(absolute){
			int sign = (int) MiscOperators.posOrNeg(motionData[1]);
			motionData[1] += energy;
			if(sign != 0 && MiscOperators.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}else{
			int sign = (int) MiscOperators.posOrNeg(motionData[1]);
			motionData[1] += energy * MiscOperators.posOrNeg(motionData[1]);
			if(MiscOperators.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}
	}

	@Override
	public void setMember(GearTypes membIn){
		member = membIn;
	}

	@Override
	public GearTypes getMember(){
		return member;
	}

}
