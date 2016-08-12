package com.Da_Technomancer.crossroads.tileentities.heat;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.AbstractInventory;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class SaltReactorTileEntity extends AbstractInventory implements ITickable {

	private FluidStack content = null;
	private FluidStack dContent = null;
	private static final int WATER_USE = 200;
	private static final int CAPACITY = 16 * WATER_USE;
	private boolean init = false;
	private double temp;
	private ItemStack inventory = null;

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeGenForCoords(getPos()).getFloatTemperature(getPos());
			init = true;
		}
		
		if(dContent != null && dContent.amount >= WATER_USE && CAPACITY - (content == null ? 0 : content.amount) >= WATER_USE && inventory != null && inventory.getItem() == ModItems.dustSalt){
			--temp;
			if((dContent.amount -= WATER_USE) <= 0){
				dContent = null;
			}
			
			if(--inventory.stackSize <= 0){
				inventory = null;
			}
			
			if(content == null){
				content = new FluidStack(FluidRegistry.WATER, WATER_USE);
			}else{
				content.amount += WATER_USE;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
		if(nbt.hasKey("dCon")){
			dContent = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("dCon"));
		}
		
		
		this.init = nbt.getBoolean("init");
		this.temp = nbt.getDouble("temp");

		if(nbt.hasKey("inv")){
			inventory = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		if(dContent != null){
			nbt.setTag("dCon", dContent.writeToNBT(new NBTTagCompound()));
		}

		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		
		if(inventory != null){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}
		
		return nbt;
	}
	
	private final IFluidHandler innerFluidHandler = new InnerFluidHandler();
	private final IFluidHandler waterFluidHandler = new WaterFluidHandler();
	private final IFluidHandler dWaterFluidHandler = new DWaterFluidHandler();
	private final IHeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if(facing == null){
				return (T) innerFluidHandler;
			}
			
			if(facing != EnumFacing.UP){
				return (T) waterFluidHandler;
			}
			
			return (T) dWaterFluidHandler;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing == EnumFacing.DOWN || facing == null){
			return (T) heatHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN) {
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing == EnumFacing.DOWN || facing == null){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return (index == 0) ? inventory : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(index != 0){
			return null;
		}
		
		ItemStack stack = inventory.splitStack(count);
		
		if(inventory.stackSize <= 0){
			inventory = null;
		}
		//Is this even needed?
		markDirty();
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index != 0){
			return null;
		}
		
		ItemStack stack = inventory;
		inventory = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index != 0){
				return;
		}

		inventory = stack;
		inventory.stackSize = Math.min(inventory.stackSize, getInventoryStackLimit());
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 16;
	}

	@Override
	public String getName() {
		return "container.saltReactor";
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index != 0){
			return false;
		}

		return stack.getItem() == ModItems.dustSalt;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return false;
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
		inventory = null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	private class InnerFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] {new FluidTankProperties(content, CAPACITY, false, true), new FluidTankProperties(dContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if(resource == null || resource.getFluid() != BlockDistilledWater.getDistilledWater()){
				return 0;
			}
			
			int maxFill = Math.min(resource.amount, CAPACITY - (dContent == null ? 0 : dContent.amount));
			if(doFill){
				if(dContent == null){
					dContent = new FluidStack(BlockDistilledWater.getDistilledWater(), maxFill);
				}else{
					dContent.amount += maxFill;
				}
			}
			
			return maxFill;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if(content == null || resource == null || resource.getFluid() != FluidRegistry.WATER){
				return null;
			}
			
			int maxDrain = Math.min(resource.amount, content.amount);
			if(doDrain && (content.amount -= maxDrain) <= 0){
				content = null;
			}
			
			return new FluidStack(FluidRegistry.WATER, maxDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			if(content == null || maxDrain == 0){
				return null;
			}
			
			maxDrain = Math.min(maxDrain, content.amount);
			if(doDrain && (content.amount -= maxDrain) <= 0){
				content = null;
			}
			
			return new FluidStack(FluidRegistry.WATER, maxDrain);
		}
	}
	
	private class WaterFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] {new FluidTankProperties(content, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if(content == null || resource == null || resource.getFluid() != FluidRegistry.WATER){
				return null;
			}
			
			int maxDrain = Math.min(resource.amount, content.amount);
			if(doDrain && (content.amount -= maxDrain) <= 0){
				content = null;
			}
			
			return new FluidStack(FluidRegistry.WATER, maxDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			if(content == null || maxDrain == 0){
				return null;
			}
			
			maxDrain = Math.min(maxDrain, content.amount);
			if(doDrain && (content.amount -= maxDrain) <= 0){
				content = null;
			}
			
			return new FluidStack(FluidRegistry.WATER, maxDrain);
		}
	}
	
	private class DWaterFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] {new FluidTankProperties(dContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if(resource == null || resource.getFluid() != BlockDistilledWater.getDistilledWater()){
				return 0;
			}
			
			int maxFill = Math.min(resource.amount, CAPACITY - (dContent == null ? 0 : dContent.amount));
			if(doFill){
				if(dContent == null){
					dContent = new FluidStack(BlockDistilledWater.getDistilledWater(), maxFill);
				}else{
					dContent.amount += maxFill;
				}
			}
			
			return maxFill;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return null;
		}
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeGenForCoords(getPos()).getFloatTemperature(getPos());
				init = true;
			}
		}

		@Override
		public double getTemp() {
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn) {
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat) {
			init();
			temp += heat;

		}
	}
}
