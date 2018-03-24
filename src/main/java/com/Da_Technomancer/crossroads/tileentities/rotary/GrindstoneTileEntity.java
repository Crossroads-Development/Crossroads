package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class GrindstoneTileEntity extends TileEntity implements ITickable{

	private ItemStack[] inventory =  {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

	private int progress = 0;
	public static final int REQUIRED = 100;

	private final double[] motionData = new double[4];
	private final double[] physData = new double[] {125, 1};

	private void runMachine(){
		if(progress == REQUIRED){
			return;
		}

		double used = Math.min(Math.floor(Math.abs(motionData[1])), REQUIRED - progress);
		progress += used;
		axleHandler.addEnergy(-used, false, false);
	}

	private void createOutput(ItemStack[] outputs){
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
			if(!inventory[0].isEmpty()){
				ItemStack[] output = getOutput();
				if(output == null){
					progress = 0;
					return;
				}
				runMachine();
				if(progress == REQUIRED){
					createOutput(output);
				}
			}else{
				progress = 0;
			}
		}
	}

	private ItemStack[] getOutput(){
		for(Entry<RecipePredicate<ItemStack>, ItemStack[]> recipe: RecipeHolder.grindRecipes.entrySet()){
			if(recipe.getKey().test(inventory[0])){
				return recipe.getValue();
			}
		}
		return null;
	}

	public void dropItems(){
		for (int i = 0; i < 4; i++){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory[i]);
			inventory[i] = ItemStack.EMPTY;
		}
		markDirty();
	}

	private final IItemHandler itemOutHandler = new ItemOutHandler();
	private final IItemHandler itemInHandler = new ItemInHandler();
	private final AllItemHandler itemAllHandler = new AllItemHandler();
	private final IAxleHandler axleHandler = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return side != EnumFacing.UP;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(side == EnumFacing.DOWN){
				return (T) itemOutHandler;
			}
			if(side == null){
				return (T) itemAllHandler;
			}
			if(side != EnumFacing.UP){
				return (T) itemInHandler;
			}
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(cap, side);
	}

	private class AllItemHandler implements IItemHandlerModifiable{

		@Override
		public int getSlots(){
			return 4;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return inventory[slot];
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot == 0){
				if(!inventory[slot].isEmpty() && !stack.isEmpty() && (!ItemStack.areItemsEqual(stack, inventory[slot]) || !ItemStack.areItemStackTagsEqual(stack, inventory[slot]))){
					return stack;
				}
				int oldCount = inventory[slot].getCount();
				int cap = Math.min(stack.getCount(), stack.getMaxStackSize() - oldCount);
				ItemStack out = stack.copy();
				out.setCount(stack.getCount() - cap);

				if(!simulate){
					markDirty();
					inventory[slot] = stack.copy();
					inventory[slot].setCount(cap + oldCount);
				}
				return out;
			}else{
				return stack;
			}
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			int cap = Math.min(amount, inventory[slot].getCount());
			if(simulate){
				return new ItemStack(inventory[slot].getItem(), cap, inventory[slot].getMetadata());
			}
			markDirty();
			return inventory[slot].splitStack(cap);

		}

		@Override
		public int getSlotLimit(int slot){
			return 64; 
		}

		@Override
		public void setStackInSlot(int slot, ItemStack stack){
			inventory[slot] = stack;
			markDirty();
		}
	}

	private class ItemOutHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 3;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			if(slot > 2 || slot < 0){
				return ItemStack.EMPTY;
			}
			return inventory[slot + 1];
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot < 0 || slot > 2){
				return ItemStack.EMPTY;
			}
			int cap = Math.min(amount, inventory[slot + 1].getCount());
			if(simulate){
				return new ItemStack(inventory[slot + 1].getItem(), cap, inventory[slot + 1].getMetadata());
			}
			markDirty();
			return inventory[slot + 1].splitStack(cap);
		}

		@Override
		public int getSlotLimit(int slot){
			return 64;
		}
	}

	private class ItemInHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory[0] : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || (!inventory[0].isEmpty() && !stack.isEmpty() && (!ItemStack.areItemsEqual(stack, inventory[0]) || !ItemStack.areItemStackTagsEqual(stack, inventory[0])))){
				return stack;
			}
			int oldCount = inventory[0].getCount();
			int cap = Math.min(stack.getCount(), stack.getMaxStackSize() - oldCount);
			ItemStack out = stack.copy();
			out.setCount(stack.getCount() - cap);

			if(!simulate){
				markDirty();
				inventory[0] = stack.copy();
				inventory[0].setCount(cap + oldCount);
			}
			return out;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 64;
		}
	}

	private class AxleHandler implements IAxleHandler{

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		private double rotRatio;
		private byte updateKey;

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * Math.signum(motionData[1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}
	}

	public int getProgress(){
		return progress;
	}

	public void setProgress(int value){
		progress = value;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		for(int i = 0; i < 4; ++i){
			if(!inventory[i].isEmpty()){
				NBTTagCompound stackTag = new NBTTagCompound();
				inventory[i].writeToNBT(stackTag);
				nbt.setTag("inv" + i, stackTag);
				nbt.setDouble("motion" + i, motionData[i]);
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
			motionData[i] = nbt.getDouble("motion" + i);
		}
	}
}
