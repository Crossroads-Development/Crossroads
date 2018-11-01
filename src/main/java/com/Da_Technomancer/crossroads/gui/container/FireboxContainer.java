package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import net.minecraft.inventory.IInventory;

public class FireboxContainer extends MachineContainer{

	public FireboxContainer(IInventory playerInv, FireboxTileEntity te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlotToContainer(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public int[] getInvStart(){
		return new int[] {8, 54};
	}
}
