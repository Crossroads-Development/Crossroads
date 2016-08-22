package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.AbstractInventory;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class CoalHeaterTileEntity extends AbstractInventory implements ITickable{

	private ItemStack inventory = null;
	private int burnTime;
	private double temp;
	private boolean init = false;
	private int ticksExisted = 0;

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
			init = true;
		}

		if(++ticksExisted % 10 == 0 && worldObj.getTileEntity(pos.offset(EnumFacing.UP)) != null && worldObj.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			double reservePool = temp * HeatConductors.COPPER.getRate();
			temp -= reservePool;

			IHeatHandler handler = getWorld().getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN);
			reservePool += handler.getTemp() * HeatConductors.COPPER.getRate();
			handler.addHeat(-(handler.getTemp() * HeatConductors.COPPER.getRate()));
			reservePool /= 2;
			temp += reservePool;
			handler.addHeat(reservePool);
		}

		if(burnTime != 0){
			markDirty();
			temp += 1;
			--burnTime;
		}

		if(burnTime == 0 && inventory != null && inventory.getItem() == Items.COAL){
			markDirty();
			if(--inventory.stackSize == 0){
				inventory = null;
			}

			burnTime = 1600;
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		burnTime = nbt.getInteger("burn");
		if(nbt.hasKey("inv")){
			inventory = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		nbt.setInteger("burn", burnTime);
		if(inventory != null){
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

		return super.hasCapability(capability, facing);
	}

	private IHeatHandler handler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return (T) handler;
		}

		return super.getCapability(capability, facing);
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
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
		return index == 0 ? inventory : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(index == 0 && inventory != null){
			ItemStack stack = inventory.splitStack(count);
			if(inventory.stackSize == 0){
				inventory = null;
			}
			markDirty();
			return stack;
		}

		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = inventory;
		inventory = null;
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
		return index == 0 && stack.getItem() == Items.COAL;
	}

	@Override
	public int getField(int id){
		if(id == 0){
			return this.burnTime;
		}else{
			return 0;
		}
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			this.burnTime = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public void clear(){
		inventory = null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public String getName(){
		return "container.coalHeater";
	}

}
