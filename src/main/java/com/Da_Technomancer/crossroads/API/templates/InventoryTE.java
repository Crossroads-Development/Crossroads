package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class InventoryTE extends ModuleTE implements ISidedInventory{

	protected final ItemStack[] inventory;
	public final FluidSlotManager[] fluidManagers = new FluidSlotManager[fluidTanks()];
	public final IntReferenceHolder rotaryReference;
	public final IntReferenceHolder heatReference;


	public InventoryTE(TileEntityType<? extends InventoryTE> type, int invSize){
		super(type);
		inventory = new ItemStack[invSize];
		for(int i = 0; i < invSize; i++){
			inventory[i] = ItemStack.EMPTY;
		}
		for(int i = 0; i < fluids.length; i++){
			fluidManagers[i] = new FluidSlotManager(fluids[i], fluidProps[i].capacity);
		}
		if(useRotary()){
			rotaryReference = IntReferenceHolder.single();
			rotaryReference.set((int) Math.round(motData[0] * 100D));
		}else{
			rotaryReference = null;
		}
		if(useHeat()){
			heatReference = IntReferenceHolder.single();
			heatReference.set((int) temp);
		}else{
			heatReference = null;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isEmpty()){
				CompoundNBT stackTag = new CompoundNBT();
				inventory[i].write(stackTag);
				nbt.put("inv_" + i, stackTag);
			}
		}
		return nbt;
	}

	@Override
	public void markDirty(){
		super.markDirty();
		for(int i = 0; i < fluidManagers.length; i++){
			fluidManagers[i].updateState(fluids[i]);
		}
		if(useRotary()){
			rotaryReference.set((int) Math.round(motData[0] * 100D));
		}
		if(useHeat()){
			heatReference.set((int) temp);
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(nbt.contains("inv_" + i)){
				inventory[i] = ItemStack.read(nbt.getCompound("inv_" + i));
			}
		}
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
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
		return inventory[index].split(count);
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

	@Override
	public int getInventoryStackLimit(){
		return inventory.length == 0 ? 0 : 64;
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
	public boolean canInsertItem(int index, ItemStack stack, Direction direction){
		return isItemValidForSlot(index, stack);
	}

	@Override
	public int[] getSlotsForFace(Direction side){
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
		private final Direction dir;

		public ItemHandler(@Nullable Direction dir){
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
			if(isItemValidForSlot(slot, stack) && (inventory[slot].isEmpty() || BlockUtil.sameItem(stack, inventory[slot]))){
				int oldCount = inventory[slot].getCount();
				int moved = Math.min(stack.getCount(), stack.getMaxStackSize() - oldCount);
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
				return new ItemStack(inventory[slot].getItem(), moved, inventory[slot].hasTag() ? inventory[slot].getTag().copy() : null);
			}
			markDirty();
			return inventory[slot].split(moved);
		}

		@Override
		public int getSlotLimit(int slot){
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return isItemValidForSlot(slot, stack);
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
