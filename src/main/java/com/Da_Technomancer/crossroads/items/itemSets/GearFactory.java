package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.ToggleGear;
import com.Da_Technomancer.crossroads.blocks.technomancy.CounterGear;

public class GearFactory{

	public static final HashMap<GearTypes, BasicGear> BASIC_GEARS = new HashMap<GearTypes, BasicGear>();
	public static final HashMap<GearTypes, LargeGear> LARGE_GEARS = new HashMap<GearTypes, LargeGear>();
	public static final HashMap<GearTypes, ToggleGear> TOGGLE_GEARS = new HashMap<GearTypes, ToggleGear>();
	public static final HashMap<GearTypes, CounterGear> COUNTER_GEARS = new HashMap<GearTypes, CounterGear>();

	public static void init(){
		for(GearTypes typ : GearTypes.values()){
			BASIC_GEARS.put(typ, new BasicGear(typ));
			LARGE_GEARS.put(typ, new LargeGear(typ));
			TOGGLE_GEARS.put(typ, new ToggleGear(typ));
			ModBlocks.blockAddQue(TOGGLE_GEARS.get(typ));
			COUNTER_GEARS.put(typ, new CounterGear(typ));
			ModBlocks.blockAddQue(COUNTER_GEARS.get(typ));
		}
	}
}
