package com.Da_Technomancer.crossroads.API.alchemy;

public enum MatterPhase{
	
	GAS(true, true, false, true),
	LIQUID(true, false, true, false),
	SOLID(false, false, false, false);
	
	private final boolean flows;
	private final boolean flowsUp;
	private final boolean flowsDown;
	private final boolean compressable;
	
	MatterPhase(boolean flows, boolean flowsUp, boolean flowsDown, boolean compressable){
		this.flows = flows;
		this.flowsUp = flowsUp;
		this.flowsDown = flowsDown;
		this.compressable = compressable;
	}
	
	public boolean flows(){
		return flows;
	}
	
	public boolean flowsUp(){
		return flows && flowsUp;
	}
	
	public boolean flowsDown(){
		return flows && flowsDown;
	}
	
	public boolean compressable(){
		return compressable;
	}
}
