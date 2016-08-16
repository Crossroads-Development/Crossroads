package com.Da_Technomancer.crossroads.container;

import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GrindstoneContainer extends Container{

	private GrindstoneTileEntity te;
	private int progress;

	public GrindstoneContainer(IInventory playerInv, GrindstoneTileEntity te){
		this.te = te;

		// input 0
		addSlotToContainer(new Slot(te, 0, 80, 17));

		// output 1-3
		for(int x = 0; x < 3; ++x){
			addSlotToContainer(new SlotFurnaceOutput(((InventoryPlayer) playerInv).player, te, 1 + x, 62 + x * 18, 53));
		}

		// Player Inventory, Slots 9-35, Slot IDs 4-30
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 31-39
		for(int x = 0; x < 9; ++x){
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUseableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			if(fromSlot < 4){
				// From TE Inventory to Player Inventory
				if(!this.mergeItemStack(current, 4, 40, true))
					return null;
			}else{
				// From Player Inventory to TE Inventory
				if(!this.mergeItemStack(current, 0, 1, false))
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
