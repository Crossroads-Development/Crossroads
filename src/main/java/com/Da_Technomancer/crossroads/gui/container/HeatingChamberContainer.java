package com.Da_Technomancer.crossroads.gui.container;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HeatingChamberContainer extends Container{

	private HeatingChamberTileEntity te;
	private int progress;

	public HeatingChamberContainer(IInventory playerInv, HeatingChamberTileEntity te){
		this.te = te;

		// Input slot, ID 0
		addSlotToContainer(new Slot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlotToContainer(new Slot(te, 1, 116, 35){
			@Override
			public boolean isItemValid(@Nullable ItemStack stack){
				return false;
			}
		});

		// Player Inventory, Slots 9-35, Slot IDs 2-28
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 29-37
		for(int x = 0; x < 9; ++x){
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			if(fromSlot == 0 || fromSlot == 1){
				// From TE Inventory to Player Inventory
				if(!this.mergeItemStack(current, 2, 38, true))
					return null;
			}else{
				// From Player Inventory to TE Inventory
				if(!this.mergeItemStack(current, 0, 2, false))
					return null;
			}

			if(current.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if(current.stackSize == previous.stackSize)
				return null;
			slot.onPickupFromSlot(playerIn, current);
		}
		return previous;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUseableByPlayer(playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data){
		this.te.setField(id, data);
	}

	@Override
	public void addListener(IContainerListener listener){
		super.addListener(listener);
		listener.sendAllWindowProperties(this, this.te);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < this.listeners.size(); ++i){
			IContainerListener icontainerlistener = (IContainerListener) this.listeners.get(i);

			if(this.progress != this.te.getField(0)){
				icontainerlistener.sendProgressBarUpdate(this, 0, this.te.getField(0));
			}
		}

		this.progress = this.te.getField(0);
	}

}
