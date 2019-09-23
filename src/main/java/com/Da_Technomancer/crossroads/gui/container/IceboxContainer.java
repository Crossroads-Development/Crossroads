package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import net.minecraft.inventory.IInventory;

public class IceboxContainer extends MachineContainer{

	public IceboxContainer(IInventory playerInv, InventoryTE te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public int[] getInvStart(){
		return new int[] {8, 54};
	}
}
