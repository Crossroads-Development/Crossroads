package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SteamBoilerTileEntity extends TileEntity implements ITickable{

	private FluidStack steamContent;
	private FluidStack waterContent;
	private ItemStack inventory;
	private final int CAPACITY = 10_000;

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		if(init == false){
			temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(getPos()).getFloatTemperature(getPos());
			init = true;
		}

		if(waterContent != null){
			runMachine();
		}

	}

	private void runMachine(){
		boolean salty = waterContent.getFluid() == FluidRegistry.WATER;
		int limit = waterContent.amount / 100;

		limit = Math.min(limit, (CAPACITY - (steamContent == null ? 0 : steamContent.amount)) / 100);
		limit = Math.min(limit, (int) Math.floor(salty ? (temp - 110) / (EnergyConverters.DEG_PER_BUCKET_STEAM * .2D) + 1 : (temp - 100) / (EnergyConverters.DEG_PER_BUCKET_STEAM * .1D) + 1));
		limit = limit < 0 ? 0 : limit;

		if(salty){
			limit = Math.min(limit, 64 - (inventory == null ? 0 : inventory.stackSize));
		}

		if(limit == 0){
			return;
		}

		waterContent.amount -= limit * 100;
		if(waterContent.amount == 0){
			waterContent = null;
		}

		if(steamContent == null){
			steamContent = new FluidStack(BlockSteam.getSteam(), limit * 100);
		}else{
			steamContent.amount += limit * 100;
		}

		if(salty){
			if(inventory == null){
				inventory = new ItemStack(ModItems.dustSalt, limit);
			}else{
				inventory.stackSize += limit;
			}
		}
		markDirty();
		temp -= limit * EnergyConverters.DEG_PER_BUCKET_STEAM * (salty ? .2D : .1D);
	}

	private boolean init = false;
	private double temp;

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		steamContent = FluidStack.loadFluidStackFromNBT(nbt);

		waterContent = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("water"));

		this.init = nbt.getBoolean("init");
		this.temp = nbt.getDouble("temp");

		inventory = nbt.hasKey("inv") ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv")) : null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(steamContent != null){
			steamContent.writeToNBT(nbt);
		}

		NBTTagCompound waterHolder = new NBTTagCompound();
		if(waterContent != null){
			waterContent.writeToNBT(waterHolder);
		}

		nbt.setTag("water", waterHolder);
		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);

		if(inventory != null){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}
		
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN){
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return true;
		}
		
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return true;
		}
		
		return super.hasCapability(capability, facing);
	}

	private final IFluidHandler waterHandler = new WaterFluidHandler();
	private final IFluidHandler steamHandler = new SteamFluidHandler();
	private final IFluidHandler innerHandler = new InnerFluidHandler();
	private final IHeatHandler heatHandler = new HeatHandler();
	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerHandler;
			}
			switch(facing){
				case NORTH:
					return (T) waterHandler;
				case SOUTH:
					return (T) waterHandler;
				case EAST:
					return (T) waterHandler;
				case WEST:
					return (T) waterHandler;
				case UP:
					return (T) steamHandler;
				case DOWN:
					return null;
			}
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)){
			return (T) heatHandler;
		}
		
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
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
			return slot == 0 ? inventory : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || inventory == null){
				return null;
			}
			
			int count = Math.min(inventory.stackSize, amount);
			
			if(!simulate){
				if((inventory.stackSize -= count) <= 0){
					inventory = null;
				}
				markDirty();
			}
			
			return amount > 0 ? new ItemStack(ModItems.dustSalt, count) : null;
		}
	}
	
	private class WaterFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(waterContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || (resource.getFluid() != FluidRegistry.WATER && resource.getFluid() != BlockDistilledWater.getDistilledWater()) || (waterContent != null && !waterContent.isFluidEqual(resource))){
				return 0;
			}
			int change = Math.min(CAPACITY - (waterContent == null ? 0 : waterContent.amount), resource.amount);
			if(doFill){
				waterContent = new FluidStack(resource.getFluid(), change + (waterContent == null ? 0 : waterContent.amount));
			}
			return change;
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

	private class SteamFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContent, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.getFluid() == BlockSteam.getSteam() && steamContent != null){
				int change = Math.min(steamContent.amount, resource.amount);

				if(doDrain){
					steamContent.amount -= change;
					if(steamContent.amount == 0){
						steamContent = null;
					}
				}

				return new FluidStack(BlockSteam.getSteam(), change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(steamContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(steamContent.amount, maxDrain);

			if(doDrain){
				steamContent.amount -= change;
				if(steamContent.amount == 0){
					steamContent = null;
				}
			}

			return new FluidStack(BlockSteam.getSteam(), change);
		}

	}

	private class InnerFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContent, CAPACITY, true, true), new FluidTankProperties(waterContent, CAPACITY, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (resource.getFluid() == FluidRegistry.WATER || resource.getFluid() == BlockDistilledWater.getDistilledWater()) && (waterContent == null || waterContent.isFluidEqual(resource))){
				int change = Math.min(CAPACITY - (waterContent == null ? 0 : waterContent.amount), resource.amount);

				if(doFill){
					waterContent = new FluidStack(resource.getFluid(), (waterContent == null ? 0 : waterContent.amount) + change);
				}

				return change;
			}else{
				return 0;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(steamContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(steamContent.amount, maxDrain);

			if(doDrain){
				steamContent.amount -= change;
				if(steamContent.amount == 0){
					steamContent = null;
				}
			}

			return new FluidStack(BlockSteam.getSteam(), change);
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.getFluid() == BlockSteam.getSteam() && steamContent != null){
				int change = Math.min(steamContent.amount, resource.amount);

				if(doDrain){
					steamContent.amount -= change;
					if(steamContent.amount == 0){
						steamContent = null;
					}
				}

				return new FluidStack(BlockSteam.getSteam(), change);
			}else{
				return null;
			}
		}

	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(getPos()).getFloatTemperature(getPos());
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
