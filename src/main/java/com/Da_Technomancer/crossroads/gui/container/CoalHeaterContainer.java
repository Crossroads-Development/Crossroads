package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FuelHeaterTileEntity;
import net.minecraft.inventory.IInventory;
import org.apache.commons.lang3.tuple.Pair;

public class CoalHeaterContainer extends MachineContainer{

	public CoalHeaterContainer(IInventory playerInv, FuelHeaterTileEntity te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlotToContainer(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public Pair<Integer, Integer> getInvStart(){
		return Pair.of(8, 54);
	}
}
