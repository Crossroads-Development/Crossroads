package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public abstract class MachineContainer extends Container{

	protected final InventoryTE te;
	protected final int[] fields;

	public MachineContainer(IInventory playerInv, InventoryTE te){
		this.te = te;
		fields = new int[te.getFieldCount()];
		addSlots();
		Pair<Integer, Integer> invStart = getInvStart();

		//Hotbar
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, invStart.getLeft() + x * 18, invStart.getRight() + 58));
		}

		//Main player inv
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, invStart.getLeft() + x * 18, invStart.getRight() + y * 18));
			}
		}
	}

	protected abstract void addSlots();

	protected int slotCount(){
		return te.inventory.length;
	}

	/**
	 * Gets the position the inventory menu slots start at
	 * @return A pair (x, y) where the player inventory slots start
	 */
	protected Pair<Integer, Integer> getInvStart(){
		return Pair.of(8, 84);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			//fromSlot < slotCount means TE -> Player, else Player -> TE input slots
			if(fromSlot < slotCount() ? !mergeItemStack(current, slotCount(), 36 + slotCount(), true) : !mergeItemStack(current, 0, slotCount(), false)){
				return ItemStack.EMPTY;
			}

			if(current.isEmpty()){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}

			if(current.getCount() == previous.getCount()){
				return ItemStack.EMPTY;
			}
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
		te.setField(id, data);
	}

	@Override
	public void addListener(IContainerListener listener){
		super.addListener(listener);
		listener.sendAllWindowProperties(this, te);
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < fields.length; i++){
			if(fields[i] != te.getField(i)){
				fields[i] = te.getField(i);
				for(IContainerListener listener : listeners){
					listener.sendWindowProperty(this, i, fields[i]);
				}
			}
		}
	}

	protected static class StrictSlot extends Slot{

		public StrictSlot(IInventory te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack){
			return inventory.isItemValidForSlot(0, stack);
		}
	}

	protected static class OutputSlot extends Slot{

		public OutputSlot(IInventory te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack){
			return false;
		}
	}
}
