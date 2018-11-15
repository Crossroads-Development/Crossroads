package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BeamExtractorTileEntity extends BeamRenderTE implements IInventory{

	private ItemStack inv = ItemStack.EMPTY;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!inv.isEmpty()){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		inv = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
	}

	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != world.getBlockState(pos).getValue(EssentialsProperties.FACING)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public boolean isEmpty(){
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index == 0 ? inv : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(index == 0){
			markDirty();
			return inv.splitStack(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index != 0){
			return ItemStack.EMPTY;
		}
		markDirty();
		ItemStack held = inv;
		inv = ItemStack.EMPTY;
		return held;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			inv = stack;
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
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
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && RecipeHolder.beamExtractRecipes.containsKey(stack.getItem());
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
		inv = ItemStack.EMPTY;
		markDirty();
	}

	@Override
	public String getName(){
		return "container.beam_extractor";
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TextComponentTranslation(getName());
	}

	@Override
	public boolean hasCustomName(){
		return false;
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inv : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack.isEmpty() || inv.getCount() >= getSlotLimit(0) || !inv.isEmpty() && !inv.isItemEqual(stack) || !(RecipeHolder.beamExtractRecipes.containsKey(stack.getItem()))){
				return stack;
			}

			int moved = Math.min(getSlotLimit(0) - inv.getCount(), stack.getCount());

			if(!simulate){
				if(inv.isEmpty()){
					inv = new ItemStack(stack.getItem(), moved, stack.getMetadata());
				}else{
					inv.grow(moved);
				}
				markDirty();
			}

			return stack.getCount() == moved ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - moved, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 4 : 0;
		}
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		EnumFacing dir = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		if(!inv.isEmpty() && RecipeHolder.beamExtractRecipes.containsKey(inv.getItem())){
			BeamUnit mag = RecipeHolder.beamExtractRecipes.get(inv.getItem());
			inv.shrink(1);
			if(beamer[dir.getIndex()].emit(mag, world)){
				refreshBeam(dir.getIndex());
			}
		}else if(beamer[dir.getIndex()].emit(null, world)){
			refreshBeam(dir.getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[6];
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = true;
		return out;
	}
}
