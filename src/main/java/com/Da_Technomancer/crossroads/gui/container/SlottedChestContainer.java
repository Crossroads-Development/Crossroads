package com.Da_Technomancer.crossroads.gui.container;

import java.util.Set;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;
import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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

	@Override
	protected void resetDrag(){
		super.resetDrag();
		dragEvent = 0;
		dragSlots.clear();
	}

	private int dragEvent;
	private final Set<Slot> dragSlots = Sets.<Slot> newHashSet();
	private int dragMode = -1;

	/**
	 * This abomination of a method is copied from the minecraft source code. It is slightly modified to set filters where necessary (and sometimes even when not, because I can't be bothered to reverse engineer this thing).
	 */
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
		ItemStack itemstack = ItemStack.EMPTY;
		InventoryPlayer inventoryplayer = player.inventory;

		if(clickTypeIn == ClickType.QUICK_CRAFT){
			int i = this.dragEvent;
			dragEvent = getDragEvent(dragType);

			if((i != 1 || this.dragEvent != 2) && i != this.dragEvent){
				resetDrag();
			}else if(inventoryplayer.getItemStack().isEmpty()){
				resetDrag();
			}else if(this.dragEvent == 0){
				dragMode = extractDragMode(dragType);

				if(isValidDragMode(dragMode, player)){
					dragEvent = 1;
					dragSlots.clear();
				}else{
					resetDrag();
				}
			}else if(this.dragEvent == 1){
				Slot slot = inventorySlots.get(slotId);
				ItemStack itemstack1 = inventoryplayer.getItemStack();

				if(slot != null && canAddItemToSlotLocked(slot, itemstack1, true) && slot.isItemValid(itemstack1) && (this.dragMode == 2 || itemstack1.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot)){
					dragSlots.add(slot);
				}
			}else if(dragEvent == 2){
				if(!dragSlots.isEmpty()){
					ItemStack itemstack5 = inventoryplayer.getItemStack().copy();
					int l = inventoryplayer.getItemStack().getCount();

					for(Slot slot1 : dragSlots){
						ItemStack itemstack2 = inventoryplayer.getItemStack();

						if(slot1 != null && canAddItemToSlotLocked(slot1, itemstack2, true) && slot1.isItemValid(itemstack2) && (this.dragMode == 2 || itemstack2.getCount() >= dragSlots.size()) && canDragIntoSlot(slot1)){
							ItemStack itemstack3 = itemstack5.copy();
							int j = slot1.getHasStack() ? slot1.getStack().getCount() : 0;
							computeStackSize(dragSlots, dragMode, itemstack3, j);
							int k = Math.min(itemstack3.getMaxStackSize(), slot1.getItemStackLimit(itemstack3));

							if(itemstack3.getCount() > k){
								itemstack3.setCount(k);
							}

							l -= itemstack3.getCount() - j;
							slot1.putStack(itemstack3);
							if(te.isInventoryType(slot1.inventory) && te.lockedInv[slot1.getSlotIndex()].isEmpty()){
								te.lockedInv[slot1.getSlotIndex()] = slot1.getStack().copy();
								te.lockedInv[slot1.getSlotIndex()].setCount(1);
								te.filterChanged();
							}
						}
					}

					itemstack5.setCount(l);
					inventoryplayer.setItemStack(itemstack5);
				}

				resetDrag();
			}else{
				resetDrag();
			}
		}else if(this.dragEvent != 0){
			resetDrag();
		}else if((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)){
			if(slotId == -999){
				if(!inventoryplayer.getItemStack().isEmpty()){
					if(dragType == 0){
						player.dropItem(inventoryplayer.getItemStack(), true);
						inventoryplayer.setItemStack(ItemStack.EMPTY);
					}

					if(dragType == 1){
						player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
					}
				}
			}else if(clickTypeIn == ClickType.QUICK_MOVE){
				if(slotId < 0){
					return ItemStack.EMPTY;
				}

				Slot slot6 = this.inventorySlots.get(slotId);
				//Clear filter on shift left empty click
				if(te.isInventoryType(slot6.inventory) && !slot6.getHasStack()){
					te.cleanPreset(slot6.getSlotIndex());
				}

				if(slot6 != null && slot6.canTakeStack(player)){
					ItemStack itemstack10 = transferStackInSlot(player, slotId);

					if(!itemstack10.isEmpty()){
						Item item = itemstack10.getItem();
						itemstack = itemstack10.copy();

						if(slot6.getStack().getItem() == item){
							retrySlotClick(slotId, dragType, true, player);
						}
					}
				}
			}else{
				if(slotId < 0){
					return ItemStack.EMPTY;
				}

				Slot slot7 = inventorySlots.get(slotId);

				if(slot7 != null){
					ItemStack itemstack11 = slot7.getStack();
					ItemStack itemstack13 = inventoryplayer.getItemStack();

					if(!itemstack11.isEmpty()){
						itemstack = itemstack11.copy();
					}

					if(itemstack11.isEmpty()){
						if(!itemstack13.isEmpty() && slot7.isItemValid(itemstack13) && canAddItemToSlotLocked(slot7, itemstack13, false)){
							int l2 = dragType == 0 ? itemstack13.getCount() : 1;

							if(l2 > slot7.getItemStackLimit(itemstack13)){
								l2 = slot7.getItemStackLimit(itemstack13);
							}

							slot7.putStack(itemstack13.splitStack(l2));
							if(te.isInventoryType(slot7.inventory) && te.lockedInv[slot7.getSlotIndex()].isEmpty()){
								te.lockedInv[slot7.getSlotIndex()] = slot7.getStack().copy();
								te.lockedInv[slot7.getSlotIndex()].setCount(1);
								te.filterChanged();
							}
						}
					}else if(slot7.canTakeStack(player)){
						if(itemstack13.isEmpty()){
							if(itemstack11.isEmpty()){
								slot7.putStack(ItemStack.EMPTY);
								inventoryplayer.setItemStack(ItemStack.EMPTY);
							}else{
								int k2 = dragType == 0 ? itemstack11.getCount() : (itemstack11.getCount() + 1) / 2;
								inventoryplayer.setItemStack(slot7.decrStackSize(k2));

								if(itemstack11.isEmpty()){
									slot7.putStack(ItemStack.EMPTY);
								}

								slot7.onTake(player, inventoryplayer.getItemStack());
							}
						}else if(slot7.isItemValid(itemstack13)){
							if(itemstack11.getItem() == itemstack13.getItem() && itemstack11.getMetadata() == itemstack13.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack11, itemstack13)){
								int j2 = dragType == 0 ? itemstack13.getCount() : 1;

								if(j2 > slot7.getItemStackLimit(itemstack13) - itemstack11.getCount()){
									j2 = slot7.getItemStackLimit(itemstack13) - itemstack11.getCount();
								}

								if(j2 > itemstack13.getMaxStackSize() - itemstack11.getCount()){
									j2 = itemstack13.getMaxStackSize() - itemstack11.getCount();
								}

								itemstack13.shrink(j2);
								itemstack11.grow(j2);
							}else if(itemstack13.getCount() <= slot7.getItemStackLimit(itemstack13)){
								slot7.putStack(itemstack13);
								if(te.isInventoryType(slot7.inventory) && te.lockedInv[slot7.getSlotIndex()].isEmpty()){
									te.lockedInv[slot7.getSlotIndex()] = slot7.getStack().copy();
									te.lockedInv[slot7.getSlotIndex()].setCount(1);
									te.filterChanged();
								}
								inventoryplayer.setItemStack(itemstack11);
							}
						}else if(itemstack11.getItem() == itemstack13.getItem() && itemstack13.getMaxStackSize() > 1 && (!itemstack11.getHasSubtypes() || itemstack11.getMetadata() == itemstack13.getMetadata()) && ItemStack.areItemStackTagsEqual(itemstack11, itemstack13) && !itemstack11.isEmpty()){
							int i2 = itemstack11.getCount();

							if(i2 + itemstack13.getCount() <= itemstack13.getMaxStackSize()){
								itemstack13.grow(i2);
								itemstack11 = slot7.decrStackSize(i2);

								if(itemstack11.isEmpty()){
									slot7.putStack(ItemStack.EMPTY);
								}

								slot7.onTake(player, inventoryplayer.getItemStack());
							}
						}
					}

					slot7.onSlotChanged();
				}
			}
		}else if(clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9){
			Slot slot5 = this.inventorySlots.get(slotId);
			ItemStack itemstack9 = inventoryplayer.getStackInSlot(dragType);
			ItemStack itemstack12 = slot5.getStack();

			if(!itemstack9.isEmpty() || !itemstack12.isEmpty()){
				if(itemstack9.isEmpty()){
					if(slot5.canTakeStack(player)){
						inventoryplayer.setInventorySlotContents(dragType, itemstack12);
						slot5.putStack(ItemStack.EMPTY);
						slot5.onTake(player, itemstack12);
					}
				}else if(itemstack12.isEmpty()){
					if(slot5.isItemValid(itemstack9)){
						int k1 = slot5.getItemStackLimit(itemstack9);

						if(itemstack9.getCount() > k1){
							slot5.putStack(itemstack9.splitStack(k1));
							if(te.isInventoryType(slot5.inventory) && te.lockedInv[slot5.getSlotIndex()].isEmpty()){
								te.lockedInv[slot5.getSlotIndex()] = slot5.getStack().copy();
								te.lockedInv[slot5.getSlotIndex()].setCount(1);
								te.filterChanged();
							}
						}else{
							slot5.putStack(itemstack9);
							if(te.isInventoryType(slot5.inventory) && te.lockedInv[slot5.getSlotIndex()].isEmpty()){
								te.lockedInv[slot5.getSlotIndex()] = slot5.getStack().copy();
								te.lockedInv[slot5.getSlotIndex()].setCount(1);
								te.filterChanged();
							}
							inventoryplayer.setInventorySlotContents(dragType, ItemStack.EMPTY);
						}
					}
				}else if(slot5.canTakeStack(player) && slot5.isItemValid(itemstack9)){
					int l1 = slot5.getItemStackLimit(itemstack9);

					if(itemstack9.getCount() > l1){
						slot5.putStack(itemstack9.splitStack(l1));
						slot5.onTake(player, itemstack12);
						if(te.isInventoryType(slot5.inventory) && te.lockedInv[slot5.getSlotIndex()].isEmpty()){
							te.lockedInv[slot5.getSlotIndex()] = slot5.getStack().copy();
							te.lockedInv[slot5.getSlotIndex()].setCount(1);
							te.filterChanged();
						}
						
						if(!inventoryplayer.addItemStackToInventory(itemstack12)){
							player.dropItem(itemstack12, true);
						}
					}else{
						slot5.putStack(itemstack9);
						if(te.isInventoryType(slot5.inventory) && te.lockedInv[slot5.getSlotIndex()].isEmpty()){
							te.lockedInv[slot5.getSlotIndex()] = slot5.getStack().copy();
							te.lockedInv[slot5.getSlotIndex()].setCount(1);
							te.filterChanged();
						}
						inventoryplayer.setInventorySlotContents(dragType, itemstack12);
						slot5.onTake(player, itemstack12);
					}
				}
			}
		}else if(clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().isEmpty() && slotId >= 0){
			Slot slot4 = this.inventorySlots.get(slotId);

			if(slot4 != null && slot4.getHasStack()){
				ItemStack itemstack8 = slot4.getStack().copy();
				itemstack8.setCount(itemstack8.getMaxStackSize());
				inventoryplayer.setItemStack(itemstack8);
			}
		}else if(clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack().isEmpty() && slotId >= 0){
			Slot slot3 = this.inventorySlots.get(slotId);

			if(slot3 != null && slot3.getHasStack() && slot3.canTakeStack(player)){
				ItemStack itemstack7 = slot3.decrStackSize(dragType == 0 ? 1 : slot3.getStack().getCount());
				slot3.onTake(player, itemstack7);
				player.dropItem(itemstack7, true);
			}
		}else if(clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0){
			Slot slot2 = this.inventorySlots.get(slotId);
			ItemStack itemstack6 = inventoryplayer.getItemStack();

			if(!itemstack6.isEmpty() && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player))){
				int i1 = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
				int j1 = dragType == 0 ? 1 : -1;

				for(int i3 = 0; i3 < 2; ++i3){
					for(int j3 = i1; j3 >= 0 && j3 < this.inventorySlots.size() && itemstack6.getCount() < itemstack6.getMaxStackSize(); j3 += j1){
						Slot slot8 = this.inventorySlots.get(j3);

						if(slot8.getHasStack() && canAddItemToSlotLocked(slot8, itemstack6, true) && slot8.canTakeStack(player) && this.canMergeSlot(itemstack6, slot8)){
							ItemStack itemstack14 = slot8.getStack();

							if(i3 != 0 || itemstack14.getCount() != itemstack14.getMaxStackSize()){
								int k3 = Math.min(itemstack6.getMaxStackSize() - itemstack6.getCount(), itemstack14.getCount());
								ItemStack itemstack4 = slot8.decrStackSize(k3);
								itemstack6.grow(k3);

								if(itemstack4.isEmpty()){
									slot8.putStack(ItemStack.EMPTY);
								}

								slot8.onTake(player, itemstack4);
							}
						}
					}
				}
			}

			this.detectAndSendChanges();
		}

		return itemstack;
	}

	/** Take a stack from the specified inventory slot.
	 * Also this version tries to shift click it out if it was in the chest inventory
	 * for some reason I can't remember. */
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

	/** Shift click-transfers an item.
	 * Modified to respect isItemValidForSlot */
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

				if(!(currentSlotStack.isEmpty() && (chestToPlayer || te.lockedInv[i].isEmpty())) && (doStackContentsMatch(toMerge, currentSlotStack) || (!chestToPlayer && doStackContentsMatch(te.lockedInv[i], toMerge)))){
					int totalCount = currentSlotStack.getCount() + toMerge.getCount();
					if(currentSlotStack.isEmpty()){
						slot.putStack(te.lockedInv[i].copy());
						currentSlotStack = slot.getStack();
						currentSlotStack.setCount(0);
					}
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

		if(!toMerge.isEmpty() && chestToPlayer){
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

	private boolean canAddItemToSlotLocked(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters){
		if(slotIn != null && te.isInventoryType(slotIn.inventory)){
			return (te.lockedInv[slotIn.getSlotIndex()].isEmpty() || doStackContentsMatch(te.lockedInv[slotIn.getSlotIndex()], stack)) && (slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= stack.getMaxStackSize());
		}
		boolean flag = slotIn == null || !slotIn.getHasStack();
		return !flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack) ? slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= stack.getMaxStackSize() : flag;
	}

	public static boolean doStackContentsMatch(ItemStack stackA, ItemStack stackB){
		return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}
}
