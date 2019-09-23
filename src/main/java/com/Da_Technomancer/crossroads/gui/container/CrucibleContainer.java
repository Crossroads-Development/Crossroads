package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class CrucibleContainer extends MachineContainer{

	public CrucibleContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 32));
		addSlot(new FluidSlot(this, 100, 19, 100, 54));
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
