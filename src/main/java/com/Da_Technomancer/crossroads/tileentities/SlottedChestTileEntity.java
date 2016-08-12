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

	private ItemStack[] inv = new ItemStack[54];
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		for(int i = 0; i < 54; ++i){
			if(nbt.hasKey("slot" + i)){
				inv[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("slot" + i));
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		for(int i = 0; i < 54; ++i){
			if(inv[i] != null){
				nbt.setTag("slot" + i, inv[i].writeToNBT(new NBTTagCompound()));
			}
		}
		
		return nbt;
	}
	
	public void cleanPreset(int slot){
		if(slot < 54){
			inv[slot] = null;
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
		public int getSlots() {
			return 54;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return slot < 54 ? inv[slot] : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if(slot >= 54 || stack == null || !ItemStack.areItemsEqual(stack, inv[slot])){
				return stack;
			}
			
			int change = Math.min(stack.getMaxStackSize() - inv[slot].stackSize, stack.stackSize);
			
			if(!simulate){
				inv[slot].stackSize += change;
			}
			
			return stack.stackSize == change ? null : new ItemStack(stack.getItem(), stack.stackSize - change, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if(slot >= 54 || inv[slot] == null){
				return null;
			}
			
			int change = Math.min(inv[slot].stackSize, amount);
			
			if(!simulate){
				inv[slot].stackSize -= change;
			}
			
			return change == 0 ? null : new ItemStack(inv[slot].getItem(), change, inv[slot].getMetadata());
		}
	}
	
	private class Inventory implements IInventory{

		@Override
		public String getName() {
			return "container.slottedChest";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentTranslation(getName());
		}

		@Override
		public int getSizeInventory() {
			return 54;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return index >= 54 ? null : inv[index];
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			if(index >= 54 || inv[index] == null){
				return null;
			}
			
			ItemStack stack = inv[index].splitStack(count);
			
			return stack;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			if(index >= 54){
				return null;
			}
			
			ItemStack stack = inv[index];
			if(inv[index] != null){
				inv[index].stackSize = 0;
			}
			return stack;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if(index < 54){
				inv[index] = stack;
			}
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
			SlottedChestTileEntity.this.markDirty();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return worldObj.getTileEntity(pos) == SlottedChestTileEntity.this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
		}

		@Override
		public void openInventory(EntityPlayer player) {
			
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return index < 54 && ItemStack.areItemsEqual(stack, inv[index]);
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			
		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			inv = new ItemStack[54];
		}
		
	}
}
