package com.Da_Technomancer.crossroads.items.item_sets;

/**
 * @deprecated It's organizationally strange to have a separate class initializing a mixed set of blocks and items in a single stage
 * Next refactor, this class should go
 */
@Deprecated
public class ItemSets{

	public static void init(){
		HeatCableFactory.init();
		GearFactory.init();
		OreSetup.init();
	}
}
