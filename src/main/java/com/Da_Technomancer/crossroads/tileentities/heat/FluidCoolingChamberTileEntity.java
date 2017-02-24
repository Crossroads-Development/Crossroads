package com.Da_Technomancer.crossroads.tileentities.heat;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FluidCoolingChamberTileEntity extends TileEntity implements ITickable{

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
			temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
			init = true;
		}

		if(++ticksExisted % 10 == 0 && content != null && RecipeHolder.fluidCoolingRecipes.containsKey(content.getFluid()) && content.amount >= RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getLeft()){
			Triple<ItemStack, Double, Double> trip = RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getRight();
			if((inventory == null || (ItemStack.areItemsEqual(trip.getLeft(), inventory) && 16 - inventory.stackSize >= trip.getLeft().stackSize)) && temp < trip.getMiddle() - rand.nextInt(ECAP * trip.getRight().intValue())){
				temp += trip.getRight();
				if((content.amount -= RecipeHolder.fluidCoolingRecipes.get(content.getFluid()).getLeft()) <= 0){
					content = null;
				}
				markDirty();
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
	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP && facing != EnumFacing.DOWN){
			return (T) fluidHandler;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return (T) heatHandler;
		}
		
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return true;
		}
		
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}
		
		return super.hasCapability(capability, facing);
	}
	
	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || inventory == null || amount <= 0){
				return null;
			}
			
			int count = Math.min(inventory.stackSize, amount);
			
			ItemStack holder = inventory.copy();
			
			if(!simulate){
				inventory.splitStack(count);
				if(inventory.stackSize <= 0){
					inventory = null;
				}
				markDirty();
			}
			return count == 0 ? null : new ItemStack(holder.getItem(), count, holder.getMetadata());
		}
	}

	private class FluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
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
		public FluidStack drain(FluidStack resource, boolean doDrain){
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			return null;
		}
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
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
}