package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import net.minecraft.inventory.IInventory;

public class StampMillContainer extends MachineContainer{

	public StampMillContainer(IInventory playerInv, StampMillTileEntity te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		addSlotToContainer(new StrictSlot(te, 0, 25, 36));
		addSlotToContainer(new OutputSlot(te, 1, 125, 36));
	}
}
