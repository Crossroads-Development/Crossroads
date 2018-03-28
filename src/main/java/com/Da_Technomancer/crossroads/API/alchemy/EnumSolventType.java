package com.Da_Technomancer.crossroads.API.alchemy;

public enum EnumSolventType{
	
	POLAR("Polar"),
	NON_POLAR("Non-Polar"),
	AQUA_REGIA("Aqua Regia"),
	FLAME("Flame");
	
	private final String name;
	
	private EnumSolventType(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
