package com.Da_Technomancer.crossroads.gui.container;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.tileentities.heat.CoalHeaterTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CoalHeaterContainer extends Container{

	private CoalHeaterTileEntity te;
	private int progress;

	public CoalHeaterContainer(IInventory playerInv, CoalHeaterTileEntity te){
		this.te = te;

		// Fuel slot, ID 0
		addSlotToContainer(new Slot(te, 0, 80, 23){
			@Override
			public boolean isItemValid(@Nullable ItemStack stack){
				return te.isItemValidForSlot(0, stack);
			}
		});

		// Player Inventory, Slots 9-35, Slot IDs 1-27
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 54 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 28-36
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 112));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			if(fromSlot == 0){
				// From TE Inventory to Player Inventory
				if(!mergeItemStack(current, 1, 37, true))
					return ItemStack.EMPTY;
			}else{
				// From Player Inventory to TE Inventory
				if(!this.mergeItemStack(current, 0, 1, false))
					return ItemStack.EMPTY;
			}

			if(current.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(current.getCount() == previous.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, current);
		}
		return previous;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUsableByPlayer(playerIn);
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
			IContainerListener icontainerlistener = (IContainerListener) listeners.get(i);

			if(this.progress != this.te.getField(0)){
				icontainerlistener.sendWindowProperty(this, 0, te.getField(0));
			}
		}

		progress = te.getField(0);
	}

}
