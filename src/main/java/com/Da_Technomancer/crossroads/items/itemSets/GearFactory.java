package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.enums.GearTypes;

public class GearFactory {

	public static final HashMap<GearTypes, BasicGear> basicGears = new HashMap<GearTypes, BasicGear>();
	public static final HashMap<GearTypes, LargeGear> largeGears = new HashMap<GearTypes, LargeGear>();
	
	public static void init(){
		for(GearTypes typ : GearTypes.values()){
			basicGears.put(typ, new BasicGear(typ));
			largeGears.put(typ, new LargeGear(typ));
		}
	}
}
