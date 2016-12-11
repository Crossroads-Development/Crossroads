package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.Da_Technomancer.crossroads.API.AbstractInventory;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class GrindstoneTileEntity extends AbstractInventory implements ITickable{

	private ItemStack[] inventory = new ItemStack[4];

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

		if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) != null && worldObj.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			topGear = worldObj.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
		}

		if(worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) != null && worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			bottomGear = worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP);
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
			decrStackSize(0, 1);

			for(ItemStack stack : outputs){
				int remain = stack.stackSize;
				for(int slot : new int[] {1, 2, 3}){
					if(remain > 0 && ItemStack.areItemsEqual(inventory[slot], stack)){
						int stored = stack.getMaxStackSize() - inventory[slot].stackSize;

						inventory[slot] = new ItemStack(stack.getItem(), inventory[slot].stackSize + Math.min(stored, remain), stack.getMetadata());
						remain -= stored;
					}
				}

				for(int slot : new int[] {1, 2, 3}){
					if(remain <= 0){
						break;
					}

					if(getStackInSlot(slot) == null){
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

			int remain = stack.stackSize;
			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && ItemStack.areItemsEqual(inventory[slot], stack)){
					remain -= stack.getMaxStackSize() - inventory[slot].stackSize;

				}
			}

			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && remain > 0 && inventory[slot] == null){
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
		if(!worldObj.isRemote){
			if(inventory[0] != null && getOutput() != null){
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
		if(inventory[0] == null){
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
			return null;
		return this.inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(this.inventory[index] != null){
			ItemStack itemstack;

			if(inventory[index].stackSize <= count){
				itemstack = this.inventory[index];
				this.setInventorySlotContents(index, null);
				this.markDirty();
				return itemstack;
			}else{
				itemstack = this.inventory[index].splitStack(count);

				if(this.inventory[index].stackSize <= 0){
					this.inventory[index] = null;
				}

				this.markDirty();
				return itemstack;
			}
		}else{
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = inventory[index];
		inventory[index] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		inventory[index] = stack;
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
		return index != 0;
	}

	@Override
	public int getField(int id){
		if(id == 0){
			return this.progress;
		}else{
			return 0;
		}
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			this.progress = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public void clear(){
		inventory = new ItemStack[4];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < 4; ++i){
			if(inventory[i] != null){
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		nbt.setTag("Items", list);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("Items", 10);
		for(int i = 0; i < list.tagCount(); ++i){
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;
			this.inventory[slot] = ItemStack.loadItemStackFromNBT(stackTag);
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {1, 2, 3} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public String getName(){
		return "container.grindstone";
	}
}
