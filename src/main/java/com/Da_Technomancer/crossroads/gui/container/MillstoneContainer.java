package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import net.minecraft.inventory.IInventory;

public class MillstoneContainer extends MachineContainer{

	public MillstoneContainer(IInventory playerInv, MillstoneTileEntity te){
		super(playerInv, te);
	}

	@Override
	protected void addSlots(){
		// input 0
		addSlot(new StrictSlot(te, 0, 80, 17));

		// output 1-3
		for(int x = 0; x < 3; x++){
			addSlot(new OutputSlot(te, 1 + x, 62 + (x * 18), 53));
		}
	}
}
