package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.blocks.heat.RedstoneHeatCable;

import java.util.HashMap;

public final class HeatCableFactory{

	public static final HashMap<HeatInsulators, HeatCable> HEAT_CABLES = new HashMap<>();
	public static final HashMap<HeatInsulators, RedstoneHeatCable> REDSTONE_HEAT_CABLES = new HashMap<>();

	protected static void init(){
		HEAT_CABLES.clear();
		REDSTONE_HEAT_CABLES.clear();
		for(HeatInsulators insul : HeatInsulators.values()){
			HEAT_CABLES.put(insul, new HeatCable(insul));
			REDSTONE_HEAT_CABLES.put(insul, new RedstoneHeatCable(insul));
		}
	}
}
