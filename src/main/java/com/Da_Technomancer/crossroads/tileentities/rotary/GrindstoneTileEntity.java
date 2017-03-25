package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.gui.AbstractInventory;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class GrindstoneTileEntity extends AbstractInventory implements ITickable{

	private ItemStack[] inventory = new ItemStack[4];

	public GrindstoneTileEntity(){
		super();
		for(int i = 0; i < 4; i++){
			inventory[i] = ItemStack.EMPTY;
		}
	}
	
	private int progress = 0;
	public static final int REQUIRED = 100;
	private static final double LOWERLIMIT = 0D;
	private static final double UPPERLIMIT = 10D;

	private void runMachine(){
		if(progress == REQUIRED){
			return;
		}

		IAxleHandler topGear = null;
		IAxleHandler bottomGear = null;

		if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			topGear = world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
		}

		if(world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null && world.getTileEntity(pos.offset(EnumFacing.DOWN)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			bottomGear = world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP);
		}

		double holder = (topGear != null ? topGear.getMotionData()[0] : 0D) + (bottomGear != null ? bottomGear.getMotionData()[0] : 0D);
		double efficiency = MiscOp.findEfficiency(holder, LOWERLIMIT, UPPERLIMIT);

		if(topGear != null){
			holder = Math.round(Math.abs(topGear.getMotionData()[1] * efficiency));
			progress += holder;
			topGear.addEnergy(-holder, false, false);
		}

		if(bottomGear != null){
			holder = Math.round(Math.abs(bottomGear.getMotionData()[1] * efficiency));
			progress += holder;
			bottomGear.addEnergy(-holder, false, false);
		}

		if(progress > REQUIRED){
			progress = REQUIRED;
		}
	}

	private void createOutput(){
		ItemStack[] outputs = getOutput();

		if(canFit(outputs)){
			progress = 0;
			inventory[0].shrink(1);

			for(ItemStack stack : outputs){
				int remain = stack.getCount();
				for(int slot = 1; slot < 4; slot++){
					if(remain > 0 && ItemStack.areItemsEqual(inventory[slot], stack)){
						int stored = stack.getMaxStackSize() - inventory[slot].getCount();

						inventory[slot] = new ItemStack(stack.getItem(), inventory[slot].getCount() + Math.min(stored, remain), stack.getMetadata());
						remain -= stored;
					}
				}

				for(int slot = 1; slot < 4; slot++){
					if(remain <= 0){
						break;
					}

					if(inventory[slot].isEmpty()){
						inventory[slot] = new ItemStack(stack.getItem(), Math.min(stack.getMaxStackSize(), remain), stack.getMetadata());
						remain -= Math.min(stack.getMaxStackSize(), remain);
					}
				}
			}
			markDirty();
		}
	}

	private boolean canFit(ItemStack[] outputs){

		boolean viable = true;

		ArrayList<Integer> locked = new ArrayList<Integer>();

		for(ItemStack stack : outputs){

			int remain = stack.getCount();
			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && ItemStack.areItemsEqual(inventory[slot], stack)){
					remain -= stack.getMaxStackSize() - inventory[slot].getCount();

				}
			}

			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && remain > 0 && inventory[slot].isEmpty()){
					remain -= stack.getMaxStackSize();
					locked.add(slot);
				}
			}

			if(remain > 0){
				viable = false;
				break;
			}
		}

		return viable;
	}

	@Override
	public void update(){
		if(!world.isRemote){
			if(!inventory[0].isEmpty() && getOutput() != null){
				runMachine();
			}else{
				progress = 0;
			}

			if(progress == REQUIRED){
				createOutput();
			}
		}
	}
	
	private ItemStack[] getOutput(){
		if(inventory[0].isEmpty()){
			return null;
		}
		for(Entry<ICraftingStack, ItemStack[]> recipe: RecipeHolder.grindRecipes.entrySet()){
			if(recipe.getKey().softMatch(inventory[0])){
				return recipe.getValue();
			}
		}
		return null;
	}

	@Override
	public int getSizeInventory(){
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		if(index < 0 || index >= 4)
			return ItemStack.EMPTY;
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(!inventory[index].isEmpty()){
			ItemStack itemstack = ItemStack.EMPTY;

			if(inventory[index].getCount() <= count){
				itemstack = this.inventory[index];
				inventory[index] = ItemStack.EMPTY;
				markDirty();
				return itemstack;
			}else{
				itemstack = this.inventory[index].splitStack(count);
				markDirty();
				return itemstack;
			}
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = inventory[index];
		inventory[index] = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index < 4){
			inventory[index] = stack;
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index > 0 && index < 4;
	}

	@Override
	public int getField(int id){
		if(id == 0){
			return progress;
		}else{
			return 0;
		}
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			progress = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public void clear(){
		for(int i = 0; i < 4; i++){
			inventory[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		for(int i = 0; i < 4; ++i){
			if(!inventory[i].isEmpty()){
				NBTTagCompound stackTag = new NBTTagCompound();
				inventory[i].writeToNBT(stackTag);
				nbt.setTag("inv" + i, stackTag);
			}
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		for(int i = 0; i < 4; ++i){
			if(nbt.hasKey("inv" + i)){
				inventory[i] = new ItemStack(nbt.getCompoundTag("inv" + i));
			}
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {1, 2, 3} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public String getName(){
		return "container.grindstone";
	}

	@Override
	public boolean isEmpty(){
		for(int i = 0; i < 4; i++){
			if(!inventory[i].isEmpty()){
				return false;
			}
		}
		return true;
	}
}
