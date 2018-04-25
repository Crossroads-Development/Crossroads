package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SamplingBenchTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SamplingBenchContainer extends Container{

	private final SamplingBenchTileEntity te;

	public SamplingBenchContainer(IInventory playerInv, SamplingBenchTileEntity te){
		this.te = te;
		// Glassware slot, ID 0
		addSlotToContainer(new Slot(te.inv, 0, 8, 35){
			@Override
			public boolean isItemValid(ItemStack stack){
				return stack.getItem() instanceof AbstractGlassware;
			}
		});

		// Player Inventory, Slots 9-35, Slot IDs 1-27
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 28-36
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return playerIn.getDistanceSq(te.getPos().add(0.5, 0.5, 0.5)) <= 64;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			if(fromSlot == 0){
				// From TE Inventory to Player Inventory
				if(!mergeItemStack(current, 1, 37, true))
					return ItemStack.EMPTY;
			}else{
				// From Player Inventory to TE Inventory
				if(!mergeItemStack(current, 0, 1, false))
					return ItemStack.EMPTY;
			}

			if(current.isEmpty()){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}
			if(current.getCount() == previous.getCount()){
				return null;
			}
			slot.onTake(playerIn, current);
		}
		return previous;
	}
}
