package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;

public final class HeatCableFactory {
	
	public static HashMap<HeatConductors, HashMap<HeatInsulators, HeatCable>> cableMap = new HashMap<HeatConductors, HashMap<HeatInsulators, HeatCable>>();
	
	public static void init(){
		for(HeatConductors cond : HeatConductors.values()){
			HashMap<HeatInsulators, HeatCable> map = new HashMap<HeatInsulators, HeatCable>();
			for(HeatInsulators insul : HeatInsulators.values()){
				map.put(insul, new HeatCable(cond, insul));
			}
			cableMap.put(cond, map);
		}
	}
}
