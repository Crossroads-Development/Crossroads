package com.Da_Technomancer.crossroads.tileentities.heat;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.AbstractInventory;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidCoolingChamberTileEntity extends AbstractInventory implements ITickable {

	private FluidStack content = null;
	private static final int CAPACITY = 16000;
	private static final int ECAP = 10;
	private boolean init = false;
	private double temp;
	private ItemStack inventory = null;
	private int ticksExisted = 0;
	private static final Random rand = new Random();
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeGenForCoords(getPos()).getFloatTemperature(getPos());
			init = true;
		}
		
		if(++ticksExisted % 10 == 0 && content != null && RecipeHolder.fluidCoolingRecipes.containsKey(content.getFluid()) && content.amount >= RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getLeft()){
			Triple<ItemStack, Double, Double> trip = RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getRight();
			if((inventory == null || (ItemStack.areItemsEqual(trip.getLeft(), inventory) && getInventoryStackLimit() - inventory.stackSize >= trip.getLeft().stackSize)) && temp < trip.getMiddle() - rand.nextInt(ECAP * trip.getRight().intValue())){
				temp += trip.getRight();
				if((content.amount -= RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getLeft()) <= 0){
					content = null;
				}
				
				if(inventory == null){
					inventory = trip.getLeft().copy();
				}else{
					inventory.stackSize += trip.getLeft().stackSize;
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
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
		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);

		if(inventory != null){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}

		return nbt;
	}

	private final IFluidHandler fluidHandler = new FluidHandler();
	private final IHeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
			return (T) fluidHandler;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing == EnumFacing.UP || facing == null){
			return (T) heatHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing == EnumFacing.UP || facing == null){
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
		return "container.fluidCoolingChamber";
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return index == 0;
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
		return false;
	}

	private class FluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if(resource == null || (content != null && !resource.isFluidEqual(content))){
				return 0;
			}

			int maxFill = Math.min(resource.amount, CAPACITY - (content == null ? 0 : content.amount));
			if(doFill){
				if(content == null){
					content = new FluidStack(resource.getFluid(), maxFill);
				}else{
					content.amount += maxFill;
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