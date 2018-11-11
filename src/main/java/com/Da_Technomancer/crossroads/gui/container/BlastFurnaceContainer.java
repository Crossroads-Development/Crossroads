package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class BlastFurnaceContainer extends MachineContainer{

	public BlastFurnaceContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlotToContainer(new StrictSlot(te, 0, 8, 35));//Gravel/Clumps
		addSlotToContainer(new StrictSlot(te, 1, 29, 20));//Carbon
		addSlotToContainer(new OutputSlot(te, 2, 44, 53));//Slag
		addSlotToContainer(new FluidSlot(this, 98, 18, 98, 53));
	}

	@Override
	protected int slotCount(){
		return 5;
	}
}
