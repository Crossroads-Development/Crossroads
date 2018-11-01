package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PrototypingTableContainer extends MachineContainer{

	public PrototypingTableContainer(IInventory playerInv, PrototypingTableTileEntity te){
		super(playerInv, te);
	}


	@Override
	protected void addSlots(){
		//Copshowium ID 0
		addSlotToContainer(new StrictSlot(te, 0, 134, 78));

		//Template ID 1
		addSlotToContainer(new StrictSlot(te, 1, 152, 78));

		//Output ID 2
		addSlotToContainer(new OutputSlot(te, 2, 152, 108));

		//Trash ID 3
		addSlotToContainer(new StrictSlot(te, 3, 134, 108));
	}

	@Override
	protected int[] getInvStart(){
		return new int[] {8, 132};
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
		ItemStack out = super.slotClick(slotId, dragType, clickTypeIn, player);
		detectAndSendChanges();
		return out;
	}
}
