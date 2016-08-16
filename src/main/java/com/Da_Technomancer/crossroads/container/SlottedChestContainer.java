package com.Da_Technomancer.crossroads.container;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlottedChestContainer extends Container{

	private final SlottedChestTileEntity te;
	private final int numRows;

	public SlottedChestContainer(IInventory playerInventory, SlottedChestTileEntity chest){
		this.te = chest;
		this.numRows = chest.iInv.getSizeInventory() / 9;
		int i = (this.numRows - 4) * 18;

		for(int j = 0; j < this.numRows; ++j){
			for(int k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(chest.iInv, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for(int l = 0; l < 3; ++l){
			for(int j1 = 0; j1 < 9; ++j1){
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1){
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.iInv.isUseableByPlayer(playerIn);
	}

	// To be clear, empty slot means slot with itemstack of stacksize 0, blank
	// slot means null stack

	@Override
	@Nullable
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
		if(slotId >= 0){
			Slot slot = inventorySlots.get(slotId);
			// dragType 0 = left, 1 = right

			if(clickTypeIn == ClickType.PICKUP_ALL){
				// return player.inventory.getItemStack();

				ItemStack[] holder = new ItemStack[54];
				for(int i = 0; i < 54; ++i){
					if(inventorySlots.get(i).getStack() != null){
						holder[i] = inventorySlots.get(i).getStack().copy();
					}
				}
				ItemStack output = super.slotClick(slotId, dragType, clickTypeIn, player);
				for(int i = 0; i < 54; ++i){
					if(holder[i] != null && inventorySlots.get(i).getStack() == null){
						holder[i].stackSize = 0;
						inventorySlots.get(i).putStack(holder[i]);
					}
				}
				this.detectAndSendChanges();
				return output;
			}

			// All shift clicks in chest without blank slot
			if(clickTypeIn == ClickType.QUICK_MOVE && slot.getStack() != null && slotId < 54){
				if(slot.getStack().stackSize == 0 && player.inventory.getItemStack() == null){
					te.cleanPreset(slotId);
				}else if(slot.getStack().stackSize != 0){
					ItemStack stack = slot.getStack();
					ItemStack stack2 = super.slotClick(slotId, dragType, clickTypeIn, player);
					slot.putStack(new ItemStack(stack.getItem(), 0, stack.getItemDamage()));
					return stack2;
				}
			}

			// All non-shift clicks in chest without blank slot
			if(clickTypeIn == ClickType.PICKUP && slot.getStack() != null && slotId < 54){
				if(player.inventory.getItemStack() == null){
					if(slot.getStack().stackSize == 0){
						return null;
					}else{
						if(dragType == 0){
							ItemStack stack = slot.getStack().copy();
							slot.putStack(new ItemStack(stack.getItem(), 0, stack.getItemDamage(), stack.getTagCompound()));
							player.inventory.setItemStack(stack);
							return stack;
						}else{
							player.inventory.setItemStack(slot.getStack().splitStack(((int) Math.ceil(((float) slot.getStack().stackSize) / 2F))));
							return player.inventory.getItemStack();
						}
					}
				}

				if(ItemStack.areItemsEqual(slot.getStack(), player.inventory.getItemStack())){
					return super.slotClick(slotId, dragType, clickTypeIn, player);
				}

				return null;
			}
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getStack() != null && slot.getStack().stackSize != 0){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index < this.numRows * 9){
				if(!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)){
					return null;
				}
			}else if(!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)){
				return null;
			}

			if(itemstack1.stackSize == 0){
				if(index >= 54){
					slot.putStack((ItemStack) null);
				}
			}else{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	/**
	 * Modified to respect isItemValidForSlot
	 */
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection){
		boolean flag = false;
		int i = startIndex;

		if(reverseDirection){
			i = endIndex - 1;
		}

		if(stack.isStackable()){
			while(stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)){
				Slot slot = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if(itemstack != null && areItemStacksEqual(stack, itemstack)){
					int j = itemstack.stackSize + stack.stackSize;

					if(j <= stack.getMaxStackSize()){
						stack.stackSize = 0;
						itemstack.stackSize = j;
						slot.onSlotChanged();
						flag = true;
					}else if(itemstack.stackSize < stack.getMaxStackSize()){
						stack.stackSize -= stack.getMaxStackSize() - itemstack.stackSize;
						itemstack.stackSize = stack.getMaxStackSize();
						slot.onSlotChanged();
						flag = true;
					}
				}

				if(reverseDirection){
					--i;
				}else{
					++i;
				}
			}
		}

		if(stack.stackSize > 0){
			if(reverseDirection){
				i = endIndex - 1;
			}else{
				i = startIndex;
			}

			while(!reverseDirection && i < endIndex || reverseDirection && i >= startIndex){
				Slot slot1 = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();

				if(itemstack1 == null && (reverseDirection || (!reverseDirection && te.iInv.isItemValidForSlot(slot1.getSlotIndex(), stack))) && slot1.isItemValid(stack)){ // Forge:
																																											// Make
																																											// sure
																																											// to
																																											// respect
																																											// isItemValid
																																											// in
																																											// the
																																											// slot.
					slot1.putStack(stack.copy());
					slot1.onSlotChanged();
					stack.stackSize = 0;
					flag = true;
					break;
				}

				if(reverseDirection){
					--i;
				}else{
					++i;
				}
			}
		}

		return flag;
	}

	private static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB){
		return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}
}
