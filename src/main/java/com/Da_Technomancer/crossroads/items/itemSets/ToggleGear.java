package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;

public class ToggleGear extends BasicGear{

	private final boolean inverted;

	public ToggleGear(boolean inverted){
		super("gear_toggle" + (inverted ? "_inv" : ""));
		this.inverted = inverted;
	}

	@Override
	protected double shapeFactor(){
		return 0.125D / 8D;
	}

	@Override
	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4);
	}
}
