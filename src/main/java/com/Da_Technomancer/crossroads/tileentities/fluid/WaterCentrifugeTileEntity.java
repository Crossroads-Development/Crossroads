package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOperators;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
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

public class WaterCentrifugeTileEntity extends TileEntity implements ITickable{
	
	private ItemStack inv;
	private FluidStack water;
	private FluidStack dWater;
	private final int CAPACITY = 10_000;
	private final double TIP_POINT = .5D;
	private boolean neg;
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		IRotaryHandler gear;
		if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) != null && (gear = worldObj.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)) != null){
			if(Math.abs(gear.getMotionData()[0]) >= TIP_POINT && (MiscOperators.posOrNeg(gear.getMotionData()[0]) == -1) == neg){
				neg = !neg;
				if(water != null && water.amount >= 100){
					if((water.amount -= 100) == 0){
						water = null;
					}
					dWater = new FluidStack(BlockDistilledWater.getDistilledWater(), Math.min(CAPACITY, 100 + (dWater == null ? 0 : dWater.amount)));
					inv = new ItemStack(ModItems.dustSalt, Math.min(64, 1 + (inv == null ? 0 : inv.stackSize)));
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		nbt.setBoolean("neg", neg);
		
		if(water != null){
			nbt.setTag("water", water.writeToNBT(new NBTTagCompound()));
		}
		if(dWater != null){
			nbt.setTag("dWater", dWater.writeToNBT(new NBTTagCompound()));
		}
		if(inv != null){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		neg = nbt.getBoolean("neg");
		water = nbt.hasKey("water") ? FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("water")) : null;
		dWater = nbt.hasKey("dWater") ? FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("dWater")) : null;
		inv = nbt.hasKey("inv") ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv")) : null;
	}
	
	private final IFluidHandler waterHandler = new WaterHandler();
	private final IFluidHandler dWaterHandler = new dWaterHandler();
	private final IFluidHandler masterHandler = new MasterHandler();
	private final IItemHandler saltHandler = new SaltHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)){
			return true;
		}
		return super.hasCapability(cap, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == null){
			return (T) masterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing.getAxis() == Axis.X) != worldObj.getBlockState(pos).getValue(Properties.ORIENT)){
			return (T) waterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing.getAxis() == Axis.X) == worldObj.getBlockState(pos).getValue(Properties.ORIENT)){
			return (T) dWaterHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) saltHandler;
		}
		
		return super.getCapability(cap, facing);
	}
	
	private class WaterHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(water, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != FluidRegistry.WATER || resource.amount <= 0){
				return 0;
			}
			
			int cap = Math.min(resource.amount, water == null ? CAPACITY : CAPACITY - water.amount);
			
			if(doFill){
				water = new FluidStack(FluidRegistry.WATER, cap + (water == null ? 0 : water.amount));
			}
			
			return cap;
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
	
	private class dWaterHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(dWater, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || dWater == null || resource.getFluid() != BlockDistilledWater.getDistilledWater() || resource.amount <= 0){
				return null;
			}
			
			int cap = Math.min(resource.amount, dWater.amount);
			
			if(doDrain && (dWater.amount -= cap) <= 0){
				dWater = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), cap);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(dWater == null || maxDrain <= 0){
				return null;
			}
			
			int cap = Math.min(maxDrain, dWater.amount);
			
			if(doDrain && (dWater.amount -= cap) <= 0){
				dWater = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), cap);
		}
	}
	
	private class MasterHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(water, CAPACITY, true, false), new FluidTankProperties(dWater, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != FluidRegistry.WATER || resource.amount <= 0){
				return 0;
			}
			
			int cap = Math.min(resource.amount, water == null ? CAPACITY : CAPACITY - water.amount);
			
			if(doFill){
				water = new FluidStack(FluidRegistry.WATER, cap + (water == null ? 0 : water.amount));
			}
			
			return cap;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || dWater == null || resource.getFluid() != BlockDistilledWater.getDistilledWater() || resource.amount <= 0){
				return null;
			}
			
			int cap = Math.min(resource.amount, dWater.amount);
			
			if(doDrain && (dWater.amount -= cap) <= 0){
				dWater = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), cap);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(dWater == null || maxDrain <= 0){
				return null;
			}
			
			int cap = Math.min(maxDrain, dWater.amount);
			
			if(doDrain && (dWater.amount -= cap) <= 0){
				dWater = null;
			}
			
			return new FluidStack(BlockDistilledWater.getDistilledWater(), cap);
		}
	}
	
	private class SaltHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inv : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || inv == null){
				return null;
			}
			
			int cap = Math.min(amount, inv.stackSize);
			
			if(!simulate){
				if((inv.stackSize -= cap) <= 0){
					inv = null;
				}
			}
			
			return new ItemStack(ModItems.dustSalt, cap);
		}
	}
}
