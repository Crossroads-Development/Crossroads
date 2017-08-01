package com.Da_Technomancer.crossroads.API.enums;

import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

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
