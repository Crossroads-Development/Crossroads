package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;

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

public class RadiatorTileEntity extends TileEntity implements ITickable{

	private FluidStack steam = null;
	private FluidStack water = null;
	private double temp;
	private final int CAPACITY = 10_000;
	private boolean init = false;
	
	@Override
	public void update(){
		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeGenForCoords(getPos()).getFloatTemperature(getPos());
			init = true;
		}
		
		if(steam != null && steam.amount >= 100 && (water == null || CAPACITY - water.amount >= 100)){
			temp += .1D * EnergyConverters.DEG_PER_BUCKET_STEAM;
			water = new FluidStack(BlockDistilledWater.getDistilledWater(), 100 + (water == null ? 0 : water.amount));
			if((steam.amount -= 100) <= 0){
				steam = null;
			}
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		
		if(nbt.hasKey("steam")){
			steam = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("steam"));
		}
		if(nbt.hasKey("water")){
			water = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("water"));
		}
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);
		
		if(steam != null){
			nbt.setTag("steam", steam.writeToNBT(new NBTTagCompound()));
		}
		if(water != null){
			nbt.setTag("water", water.writeToNBT(new NBTTagCompound()));
		}
		
		return nbt;
	}
	
	private final HeatHandler heatHandler = new HeatHandler();
	private final SteamHandler steamHandler = new SteamHandler();
	private final WaterHandler waterHandler = new WaterHandler();
	private final InnerHandler innerHandler = new InnerHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && side != EnumFacing.UP && side != EnumFacing.DOWN){
			return true;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || side == EnumFacing.UP || side == EnumFacing.DOWN)){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null){
				return (T) innerHandler;
			}
			
			if(side == EnumFacing.UP){
				return (T) waterHandler;
			}
			
			if(side == EnumFacing.DOWN){
				return (T) steamHandler;
			}
		}
			
			
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && side != EnumFacing.UP && side != EnumFacing.DOWN){
			return (T) heatHandler;
		}
		
		return super.getCapability(cap, side);
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
	
	private class InnerHandler implements IFluidHandler{
		
		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(steam, CAPACITY, true, false), new FluidTankProperties(water, CAPACITY, false, true)};
		}
		
		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != BlockSteam.getSteam() || resource.amount == 0){
				return 0;
			}
			int filled = Math.min(resource.amount, CAPACITY - (steam == null ? 0 : steam.amount));
			
			if(doFill){
				steam = new FluidStack(BlockSteam.getSteam(), filled + (steam == null ? 0 : steam.amount));
			}
			
			return filled;
		}
		
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || water == null || resource.amount == 0 || resource.getFluid() != BlockDistilledWater.getDistilledWater()){
				return null;
			}
			int drained = Math.min(water.amount, resource.amount);
			
			if(doDrain && (water.amount -= drained) <= 0){
				water = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), drained);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(water == null || maxDrain == 0){
				return null;
			}
			int drained = Math.min(water.amount, maxDrain);
			
			if(doDrain && (water.amount -= drained) <= 0){
				water = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), drained);
		}
	}
	
	private class SteamHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(steam, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != BlockSteam.getSteam() || resource.amount == 0){
				return 0;
			}
			int filled = Math.min(resource.amount, CAPACITY - (steam == null ? 0 : steam.amount));
			
			if(doFill){
				steam = new FluidStack(BlockSteam.getSteam(), filled + (steam == null ? 0 : steam.amount));
			}
			
			return filled;
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
	
	private class WaterHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(water, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || water == null || resource.amount == 0 || resource.getFluid() != BlockDistilledWater.getDistilledWater()){
				return null;
			}
			int drained = Math.min(water.amount, resource.amount);
			
			if(doDrain && (water.amount -= drained) <= 0){
				water = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), drained);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(water == null || maxDrain == 0){
				return null;
			}
			int drained = Math.min(water.amount, maxDrain);
			
			if(doDrain && (water.amount -= drained) <= 0){
				water = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), drained);
		}
	}
}
