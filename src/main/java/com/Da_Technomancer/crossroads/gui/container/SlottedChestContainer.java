package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlottedChestContainer extends Container{

	private final SlottedChestTileEntity te;

	public SlottedChestContainer(IInventory playerInventory, SlottedChestTileEntity chest){
		this.te = chest;
		int numRows = chest.iInv.getSizeInventory() / 9;
		int i = (numRows - 4) * 18;

		for(int j = 0; j < numRows; ++j){
			for(int k = 0; k < 9; ++k){
				addSlotToContainer(new Slot(chest.iInv, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for(int l = 0; l < 3; ++l){
			for(int j1 = 0; j1 < 9; ++j1){
				addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1){
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.iInv.isUsableByPlayer(playerIn);
	}

	/** 
	 * This is almost certainly entirely broken in 1.11, and needs to be re-written TODO
	 * 
	 * To be clear, empty slot means EMPTY stack with lockedInv not EMPTY, 
	 * blank slot means EMPTY stack and EMPTY lockedInv
	 */
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
		if(slotId >= 0){
			Slot slot = inventorySlots.get(slotId);
			// dragType 0 = left, 1 = right

			if(clickTypeIn == ClickType.PICKUP_ALL){
				/*ItemStack[] holder = new ItemStack[54];
				for(int i = 0; i < 54; i++){
					holder[i] = ItemStack.EMPTY;
					if(!te.lockedInv[i].isEmpty()){
						// pass if not blank
						holder[i] = inventorySlots.get(i).getStack().copy();
					}
				}*/
				ItemStack output = super.slotClick(slotId, dragType, clickTypeIn, player);
				/*for(int i = 0; i < 54; i++){
					if(!holder[i].isEmpty() && inventorySlots.get(i).getStack().isEmpty()){
						// pass if not blank //  pass if blank
						holder[i].setCount(0);
						inventorySlots.get(i).putStack(holder[i]);
					}
				}*/
				detectAndSendChanges();
				return output;
			}

			// All shift clicks in chest without blank slot
			if(clickTypeIn == ClickType.QUICK_MOVE && slotId < 54 && !te.lockedInv[slotId].isEmpty()){
				if(slot.getStack().isEmpty() && player.inventory.getItemStack().isEmpty()){
					te.cleanPreset(slotId);
				}else if(!slot.getStack().isEmpty()){
					//ItemStack stackInSlot = slot.getStack();
					ItemStack stackOut = super.slotClick(slotId, dragType, clickTypeIn, player);
					/*if(slot.getStack().isEmpty()){
						slot.putStack(new ItemStack(stackInSlot.getItem(), 0, stackInSlot.getItemDamage()));
					}*/
					return stackOut;
				}
			}

			// All non-shift clicks in chest without blank slot
			if(clickTypeIn == ClickType.PICKUP && slotId < 54 && !te.lockedInv[slotId].isEmpty()){
				if(player.inventory.getItemStack().isEmpty()){
					if(slot.getStack().isEmpty()){
						return ItemStack.EMPTY;
					}else{
						if(dragType == 0){
							ItemStack stack = slot.getStack().copy();
							slot.putStack(ItemStack.EMPTY);
							player.inventory.setItemStack(stack);
							return stack;
						}else{
							player.inventory.setItemStack(slot.getStack().splitStack(((int) Math.ceil(((float) slot.getStack().getCount()) / 2F))));
							return player.inventory.getItemStack();
						}
					}
				}

				if(doStackContentsMatch(slot.getStack(), player.inventory.getItemStack())){
					return super.slotClick(slotId, dragType, clickTypeIn, player);
				}

				return ItemStack.EMPTY;
			}
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 * Also this version tries to shift click it out if it was in the chest inventory
	 * for some reason I can't remember.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
		ItemStack outStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && !slot.getStack().isEmpty()){
			ItemStack stackInSlot = slot.getStack();
			outStack = stackInSlot.copy();

			if(index < 54){
				if(!mergeItemStack(stackInSlot, 54, inventorySlots.size(), true)){
					return ItemStack.EMPTY;
				}
			}else if(!mergeItemStack(stackInSlot, 0, 54, false)){
				return ItemStack.EMPTY;
			}

			slot.onSlotChanged();
		}

		return outStack;
	}

	/**
	 * Shift click-transfers an item. 
	 * Modified to respect isItemValidForSlot
	 */
	@Override
	protected boolean mergeItemStack(ItemStack toMerge, int startIndex, int endIndex, boolean chestToPlayer){
		boolean flag = false;
		int i = startIndex;

		if(chestToPlayer){
			i = endIndex - 1;
		}

		if(toMerge.isStackable()){
			while(!toMerge.isEmpty() && (!chestToPlayer && i < endIndex || chestToPlayer && i >= startIndex)){
				Slot slot = inventorySlots.get(i);
				ItemStack currentSlotStack = slot.getStack();

				if(!currentSlotStack.isEmpty() && doStackContentsMatch(toMerge, currentSlotStack)){
					int totalCount = currentSlotStack.getCount() + toMerge.getCount();

					if(totalCount <= toMerge.getMaxStackSize()){
						toMerge.setCount(0);
						currentSlotStack.setCount(totalCount);
						slot.onSlotChanged();
						flag = true;
					}else if(currentSlotStack.getCount() < toMerge.getMaxStackSize()){
						toMerge.shrink(toMerge.getMaxStackSize() - currentSlotStack.getCount());
						currentSlotStack.setCount(toMerge.getMaxStackSize());
						slot.onSlotChanged();
						flag = true;
					}
				}

				if(chestToPlayer){
					--i;
				}else{
					++i;
				}
			}
		}

		if(!toMerge.isEmpty()){
			if(chestToPlayer){
				i = endIndex - 1;
			}else{
				i = startIndex;
			}

			while(!chestToPlayer && i < endIndex || chestToPlayer && i >= startIndex){
				Slot slot = inventorySlots.get(i);
				ItemStack currentSlotStack = slot.getStack();

				// Make sure to respect isItemValid in the slot.
				if(currentSlotStack.isEmpty() && (chestToPlayer || (!chestToPlayer && te.iInv.isItemValidForSlot(slot.getSlotIndex(), toMerge))) && slot.isItemValid(toMerge)){ 
					slot.putStack(toMerge.copy());
					slot.onSlotChanged();
					if(!chestToPlayer){
						te.lockedInv[i] = toMerge.copy();
						te.lockedInv[i].setCount(1);
					}
					toMerge.setCount(0);
					flag = true;
					break;
				}

				if(chestToPlayer){
					--i;
				}else{
					++i;
				}
			}
		}

		return flag;
	}

	public static boolean doStackContentsMatch(ItemStack stackA, ItemStack stackB){
		return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}
}
