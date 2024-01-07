package com.Da_Technomancer.crossroads.api.alchemy;

public enum EnumMatterPhase{

	//Order affects rendering in ReagentRenderer

	FLAME(true, true, false),
	GAS(true, true, false),
	LIQUID(true, false, true),
	SOLID(true, false, true);

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
