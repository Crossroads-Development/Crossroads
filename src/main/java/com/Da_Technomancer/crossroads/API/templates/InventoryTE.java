package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class InventoryTE extends ModuleTE implements ISidedInventory{

	protected final ItemStack[] inventory;

	public InventoryTE(int invSize){
		super();
		inventory = new ItemStack[invSize];
		for(int i = 0; i < invSize; i++){
			inventory[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isEmpty()){
				NBTTagCompound stackTag = new NBTTagCompound();
				inventory[i].writeToNBT(stackTag);
				nbt.setTag("inv_" + i, stackTag);
			}
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(nbt.hasKey("inv_" + i)){
				inventory[i] = new ItemStack(nbt.getCompoundTag("inv_" + i));
			}
		}
	}

	@Override
	public String getName(){
		return null;
	}

	@Override
	public boolean hasCustomName(){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TextComponentTranslation(getName());
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player){

	}

	@Override
	public void closeInventory(EntityPlayer player){

	}

	@Override
	public int getSizeInventory(){
		return inventory.length;
	}

	@Override
	public boolean isEmpty(){
		for(ItemStack stack : inventory){
			if(!stack.isEmpty()){
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index >= inventory.length ? ItemStack.EMPTY : inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(index >= inventory.length || inventory[index].isEmpty()){
			return ItemStack.EMPTY;
		}
		markDirty();
		return inventory[index].splitStack(count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index >= inventory.length || inventory[index].isEmpty()){
			return ItemStack.EMPTY;
		}
		markDirty();
		ItemStack removed = inventory[index];
		inventory[index]= ItemStack.EMPTY;
		return removed;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >= inventory.length){
			return;
		}
		inventory[index] = stack;
		markDirty();
	}

	protected int getSlotLimit(int slot){
		return 64;
	}

	@Override
	public int getInventoryStackLimit(){
		return inventory.length == 0 ? 0 : getSlotLimit(0);
	}

	@Override
	public void clear(){
		for(int i = 0; i < inventory.length; i++){
			inventory[i] = ItemStack.EMPTY;
		}
		if(inventory.length != 0){
			markDirty();
		}
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction){
		return isItemValidForSlot(index, stack);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		int[] out = new int[inventory.length];
		for(int i = 0; i < out.length; i++){
			out[i] = i;
		}
		return out;
	}

	protected class ItemHandler implements IItemHandlerModifiable{

		/**
		 * A direction that this should act as internally. Does not need to match the side passed to the getCapability call
		 */
		private final EnumFacing dir;

		public ItemHandler(@Nullable EnumFacing dir){
			this.dir = dir;
		}

		@Override
		public int getSlots(){
			return inventory.length;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot){
			return slot >= inventory.length ? ItemStack.EMPTY : inventory[slot];
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			if(isItemValidForSlot(slot, stack) && (inventory[slot].isEmpty() || ItemStack.areItemsEqual(stack, inventory[slot]) && ItemStack.areItemStackTagsEqual(stack, inventory[slot]))){
				int oldCount = inventory[slot].getCount();
				int moved = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), InventoryTE.this.getSlotLimit(slot)) - oldCount);
				ItemStack out = stack.copy();
				out.setCount(stack.getCount() - moved);

				if(!simulate){
					markDirty();
					inventory[slot] = stack.copy();
					inventory[slot].setCount(moved + oldCount);
				}
				return out;
			}else{
				return stack;
			}
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot >= inventory.length || !canExtractItem(slot, inventory[slot], dir)){
				return ItemStack.EMPTY;
			}

			int moved = Math.min(amount, inventory[slot].getCount());
			if(simulate){
				return new ItemStack(inventory[slot].getItem(), moved, inventory[slot].getMetadata());
			}
			markDirty();
			return inventory[slot].splitStack(moved);
		}

		@Override
		public int getSlotLimit(int slot){
			return InventoryTE.this.getSlotLimit(slot);
		}

		@Override
		public void setStackInSlot(int slot, @Nonnull ItemStack stack){
			if(slot < inventory.length){
				inventory[slot] = stack;
				markDirty();
			}
		}
	}
}
