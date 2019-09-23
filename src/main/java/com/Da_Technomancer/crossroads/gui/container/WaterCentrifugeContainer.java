package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class WaterCentrifugeContainer extends MachineContainer{

	public WaterCentrifugeContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 40, 54));//Salt
		addSlot(new FluidSlot(this, 100, 19, 100, 54));
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
