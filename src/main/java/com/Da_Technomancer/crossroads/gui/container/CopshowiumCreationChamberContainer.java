package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class CopshowiumCreationChamberContainer extends MachineContainer{

	public CopshowiumCreationChamberContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlotToContainer(new FluidSlot(this, 100, 19, 100, 54));
	}

	@Override
	protected int slotCount(){
		return 2;
	}
}