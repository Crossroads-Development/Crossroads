package com.Da_Technomancer.crossroads.API.heat;

public enum CableThemes{
	
	COPPER("ingotCopper"),
	IRON("ingotIron"),
	QUARTZ("gemQuartz"),
	DIAMOND("gemDiamond");
	
	CableThemes(String oreDict){
		IHeatHandler.OREDICT_TO_THEME.put(oreDict, this);
	}
	
	@Override
	public String toString(){
		return name().toLowerCase();
	}
}
