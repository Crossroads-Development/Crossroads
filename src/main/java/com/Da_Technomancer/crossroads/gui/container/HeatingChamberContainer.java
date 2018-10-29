package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;
import net.minecraft.inventory.IInventory;

public class HeatingChamberContainer extends MachineContainer{

	public HeatingChamberContainer(IInventory playerInv, HeatingChamberTileEntity te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		// Input slot, ID 0
		addSlotToContainer(new StrictSlot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlotToContainer(new OutputSlot(te, 1, 116, 35));
	}
}
