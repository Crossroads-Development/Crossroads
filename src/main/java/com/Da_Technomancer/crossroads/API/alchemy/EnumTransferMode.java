package com.Da_Technomancer.crossroads.API.alchemy;

public enum EnumTransferMode{
	
	INPUT(true, false),
	OUTPUT(false, true),
	BOTH(true, true),
	NONE(false, false);

	private final boolean canInput;
	private final boolean canOutput;
	
	EnumTransferMode(boolean canInput, boolean canOutput){
		this.canInput = canInput;
		this.canOutput = canOutput;
	}
	
	public boolean isInput(){
		return canInput;
	}
	
	public boolean isOutput(){
		return canOutput;
	}
}
