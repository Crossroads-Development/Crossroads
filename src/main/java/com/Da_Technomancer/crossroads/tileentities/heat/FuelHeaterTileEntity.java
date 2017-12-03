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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FuelHeaterTileEntity extends AbstractInventory implements ITickable, IInfoTE{

	private ItemStack inventory = ItemStack.EMPTY;
	private int burnTime;
	private double temp;
	private boolean init = false;

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, EnumFacing side){
		if(device instanceof OmniMeter || device == EnumGoggleLenses.RUBY || device instanceof Thermometer){
			chat.add("Temp: " + handler.getTemp() + "°C");
			if(!(device instanceof Thermometer)){
				chat.add("Biome Temp: " + EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		TileEntity upTE = world.getTileEntity(pos.offset(EnumFacing.UP));
		if(upTE != null && upTE.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			double reservePool = temp;
			temp -= reservePool;

			IHeatHandler handler = upTE.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN);
			reservePool += handler.getTemp();
			handler.addHeat(-(handler.getTemp()));
			reservePool /= 2;
			temp += reservePool;
			handler.addHeat(reservePool);
		}

		if(burnTime != 0){
			temp += 1D;
			--burnTime;
			markDirty();
		}

		if(burnTime == 0 && TileEntityFurnace.isItemFuel(inventory)){
			burnTime = TileEntityFurnace.getItemBurnTime(inventory);
			Item item = inventory.getItem();
			inventory.shrink(1);
			if(inventory.isEmpty() && item != null && item.hasContainerItem(inventory)){
				inventory = item.getContainerItem(inventory);
			}
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		burnTime = nbt.getInteger("burn");
		if(nbt.hasKey("inv")){
			inventory = new ItemStack(nbt.getCompoundTag("inv"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);
		nbt.setInteger("burn", burnTime);
		if(!inventory.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			inventory.writeToNBT(invTag);
			nbt.setTag("inv", invTag);
		}
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return true;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private IHeatHandler handler = new HeatHandler();
	private ItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return (T) handler;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(stack != null && slot == 0 && TileEntityFurnace.isItemFuel(stack) && (inventory.isEmpty() || ItemStack.areItemsEqual(stack, inventory))){
				if(inventory.isEmpty()){
					if(!simulate){
						inventory = stack.copy();
						markDirty();
					}
					return ItemStack.EMPTY;
				}
				
				int moved = Math.min(inventory.getMaxStackSize() - inventory.getCount(), stack.getCount());
				
				if(!simulate){
					inventory.grow(moved);
					markDirty();
				}
				ItemStack output = stack.copy();
				output.shrink(moved);
				return output;
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
	
	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
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
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index == 0 ? inventory : ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(index == 0 && !inventory.isEmpty()){
			ItemStack stack = inventory.splitStack(count);
			markDirty();
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = inventory;
		inventory = ItemStack.EMPTY;
		markDirty();
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			inventory = stack;
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && TileEntityFurnace.isItemFuel(stack);
	}

	@Override
	public int getField(int id){
		if(id == 0){
			return burnTime;
		}else{
			return 0;
		}
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			burnTime = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public void clear(){
		inventory = ItemStack.EMPTY;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public String getName(){
		return "container.fuel_heater";
	}

	@Override
	public boolean isEmpty(){
		return inventory.isEmpty();
	}

}
