package com.Da_Technomancer.crossroads.API.alchemy;

public enum EnumSolventType{
	
	POLAR(),
	NON_POLAR(),
	AQUA_REGIA(),
	FLAME();
	
	@Override
	public String toString(){
		String name = name();
		if(name.length() <= 1){
			return name;
		}
		name = name.charAt(0) + name.substring(1).toLowerCase();
		
		return name;
	}
}
