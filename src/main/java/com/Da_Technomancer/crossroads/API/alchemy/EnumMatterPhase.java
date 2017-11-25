package com.Da_Technomancer.crossroads.API.alchemy;

public enum EnumMatterPhase{
	
	GAS(true, true, false),
	LIQUID(true, false, true),
	SOLUTE(true, false, true),
	SOLID(false, false, false),
	FLAME(true, true, false);
	
	private final boolean flows;
	private final boolean flowsUp;
	private final boolean flowsDown;
	
	EnumMatterPhase(boolean flows, boolean flowsUp, boolean flowsDown){
		this.flows = flows;
		this.flowsUp = flowsUp;
		this.flowsDown = flowsDown;
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
}
