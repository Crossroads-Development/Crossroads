package com.Da_Technomancer.crossroads.API.heat;

import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;

public enum CableThemes{

	COPPER("ingotCopper"),
	IRON("ingotIron"),
	QUARTZ("gemQuartz"),
	DIAMOND("gemDiamond");

	CableThemes(String oreDict){
		HeatCable.OREDICT_TO_THEME.put(oreDict, this);
	}
	
	@Override
	public String toString(){
		return name().toLowerCase();
	}
}
