package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class OreCleanserContainer extends MachineContainer{

	public OreCleanserContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlotToContainer(new StrictSlot(te, 0, 26, 53));//Gravel
		addSlotToContainer(new OutputSlot(te, 1, 44, 53));//Clumps
		addSlotToContainer(new FluidSlot(this, 98, 18, 98, 53));
	}

	@Override
	protected int slotCount(){
		return 4;
	}
}
