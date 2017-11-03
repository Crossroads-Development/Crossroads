package com.Da_Technomancer.crossroads.tileentities.heat;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.gui.AbstractInventory;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import com.Da_Technomancer.crossroads.items.Thermometer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class HeatingChamberTileEntity extends AbstractInventory implements ITickable, IInfoTE{

	// 0 = Input, 1 = Output
	private ItemStack[] inventory = {ItemStack.EMPTY, ItemStack.EMPTY};
	private int progress = 0;
	private double temp;
	private boolean init = false;
	public final static int REQUIRED = 100;
	private final static int MINTEMP = 200;

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, EnumFacing side){
		if(device instanceof OmniMeter || device == EnumGoggleLenses.RUBY || device instanceof Thermometer){
			chat.add("Temp: " + heatHandler.getTemp() + "°C");
			if(!(device instanceof Thermometer)){
				chat.add("Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos) + "°C");
			}
		}
	}
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos);
			init = true;
		}

		if(temp >= 2 + MINTEMP){
			temp -= 2;
			ItemStack output = getOutput();
			if(!inventory[0].isEmpty() && !output.isEmpty()){
				progress += 2;
				if(progress >= REQUIRED){
					progress = 0;
					
					if(inventory[1].isEmpty()){
						inventory[1] = output;
					}else{
						inventory[1].grow(output.getCount());
					}
					inventory[0].shrink(1);
				}
			}else{
				progress = 0;
			}
			markDirty();
		}
	}

	private ItemStack getOutput(){
		ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(inventory[0]);

		if(stack.isEmpty()){
			return ItemStack.EMPTY;
		}

		if(!inventory[1].isEmpty() && !ItemStack.areItemsEqual(stack, inventory[1])){
			return ItemStack.EMPTY;
		}

		if(!inventory[1].isEmpty() && getInventoryStackLimit() - inventory[1].getCount() < stack.getCount()){
			return ItemStack.EMPTY;
		}

		return stack.copy();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		progress = nbt.getInteger("prog");

		if(nbt.hasKey("inv")){
			inventory[0] = new ItemStack(nbt.getCompoundTag("inv"));
		}
		if(nbt.hasKey("invO")){
			inventory[1] = new ItemStack(nbt.getCompoundTag("invO"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);
		nbt.setInteger("prog", progress);

		if(!inventory[0].isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			inventory[0].writeToNBT(invTag);
			nbt.setTag("inv", invTag);
		}
		if(!inventory[1].isEmpty()){
			NBTTagCompound invTagO = new NBTTagCompound();
			inventory[1].writeToNBT(invTagO);
			nbt.setTag("invO", invTagO);
		}
		return nbt;
	}

	private IHeatHandler heatHandler = new HeatHandler();
	private IItemHandler outputHandler = new OutputHandler();
	private IItemHandler inputHandler = new InputHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == null)){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != EnumFacing.UP){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == null)){
			return (T) heatHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != EnumFacing.UP){
			return (T) (side == EnumFacing.DOWN ? outputHandler : inputHandler);
		}

		return super.getCapability(cap, side);
	}

	private class InputHandler implements IItemHandler{

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
			if(slot == 0 && !stack.isEmpty() && (inventory[0].isEmpty() || ItemStack.areItemsEqual(inventory[0], stack))){
				int inserted = Math.min(stack.getCount(), stack.getMaxStackSize() - inventory[0].getCount());

				if(!simulate && inserted != 0){
					if(inventory[0].isEmpty()){
						inventory[0] = stack.copy();
						inventory[0].setCount(inserted);
					}else{
						inventory[0].grow(inserted);
					}
					markDirty();
				}

				if(inserted == stack.getCount()){
					return ItemStack.EMPTY;
				}

				ItemStack returned = stack.copy();
				returned.shrink(inserted);
				return returned;
			}

			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}

	private class OutputHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory[1] : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot == 0 && !inventory[1].isEmpty() && amount > 0){
				int extracted = Math.min(amount, inventory[1].getCount());
				ItemStack output = inventory[1].copy();
				output.setCount(extracted);
				if(!simulate && extracted != 0){
					inventory[1].shrink(extracted);
					markDirty();
				}
				return output;
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}


	@Override
	public int getSizeInventory(){
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index > 1 ? ItemStack.EMPTY : inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		ItemStack out = index > 1 ? ItemStack.EMPTY : inventory[index].splitStack(count);
		return out;
	}

	@Override
	public String getName(){
		return "container.heating_chamber";
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index > 1){
			return ItemStack.EMPTY;
		}

		ItemStack holder = inventory[index];
		inventory[index] = ItemStack.EMPTY;
		return holder;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index > 1){
			return;
		}
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
	public int getField(int id){
		return id == 0 ? progress : 0;
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
		inventory = new ItemStack[2];
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {1} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return index == 0 && direction != EnumFacing.DOWN;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 1 && direction == EnumFacing.DOWN;
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos);
				init = true;
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}

	}

	@Override
	public boolean isEmpty(){
		return inventory[0].isEmpty() && inventory[1].isEmpty();
	}
}
