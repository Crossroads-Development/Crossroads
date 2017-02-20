package com.Da_Technomancer.crossroads.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SlottedChestTileEntity extends TileEntity{

	public SlottedChestTileEntity(){
		for(int i = 0; i < 54; i++){
			inv[i] = ItemStack.EMPTY;
		}
	}
	
	private ItemStack[] inv = new ItemStack[54];

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		for(int i = 0; i < 54; ++i){
			if(nbt.hasKey("slot" + i)){
				inv[i] = new ItemStack(nbt.getCompoundTag("slot" + i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		for(int i = 0; i < 54; ++i){
			if(!inv[i].isEmpty()){
				nbt.setTag("slot" + i, inv[i].writeToNBT(new NBTTagCompound()));
			}
		}

		return nbt;
	}

	public void cleanPreset(int slot){
		if(slot < 54){
			inv[slot] = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(cap, facing);
	}

	public final IInventory iInv = new Inventory();
	private final InventoryHandler handler = new InventoryHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) handler;
		}

		return super.getCapability(cap, facing);
	}

	private class InventoryHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 54;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot < 54 ? inv[slot] : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot >= 54 || stack.isEmpty() || !ItemStack.areItemsEqual(stack, inv[slot])){
				return stack;
			}

			int change = Math.min(stack.getMaxStackSize() - inv[slot].getCount(), stack.getCount());

			if(!simulate){
				inv[slot].grow(change);
			}

			ItemStack out = stack.copy();
			out.shrink(change);
			return stack.getCount() == change ? ItemStack.EMPTY : out;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot >= 54 || inv[slot].isEmpty()){
				return ItemStack.EMPTY;
			}

			int change = Math.min(inv[slot].getCount(), amount);

			if(!simulate){
				inv[slot].grow(change);
			}
			ItemStack out = inv[slot].copy();
			out.setCount(change);
			
			return change == 0 ? ItemStack.EMPTY : out;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot < 54 ? 64 : 0;
		}
	}

	private class Inventory implements IInventory{

		@Override
		public String getName(){
			return "container.slottedChest";
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
		public int getSizeInventory(){
			return 54;
		}

		@Override
		public ItemStack getStackInSlot(int index){
			return index >= 54 ? ItemStack.EMPTY : inv[index];
		}

		@Override
		public ItemStack decrStackSize(int index, int count){
			if(index >= 54 || inv[index].isEmpty()){
				return ItemStack.EMPTY;
			}

			ItemStack stack = inv[index].splitStack(count);

			return stack;
		}

		@Override
		public ItemStack removeStackFromSlot(int index){
			if(index >= 54){
				return ItemStack.EMPTY;
			}

			ItemStack stack = inv[index].copy();
			inv[index].setCount(0);
			return stack;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack){
			if(index < 54){
				inv[index] = stack;
			}
		}

		@Override
		public int getInventoryStackLimit(){
			return 64;
		}

		@Override
		public void markDirty(){
			SlottedChestTileEntity.this.markDirty();
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player){
			return world.getTileEntity(pos) == SlottedChestTileEntity.this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
		}

		@Override
		public void openInventory(EntityPlayer player){

		}

		@Override
		public void closeInventory(EntityPlayer player){

		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack){
			return index < 54 && ItemStack.areItemsEqual(stack, inv[index]);
		}

		@Override
		public int getField(int id){
			return 0;
		}

		@Override
		public void setField(int id, int value){

		}

		@Override
		public int getFieldCount(){
			return 0;
		}

		@Override
		public void clear(){
			inv = new ItemStack[54];
		}

		@Override
		public boolean isEmpty(){
			for(int i = 0; i < 54; i++){
				if(!inv[i].isEmpty()){
					return false;
				}
			}
			return true;
		}
	}
}
