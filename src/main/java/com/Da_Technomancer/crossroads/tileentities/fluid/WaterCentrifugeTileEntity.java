package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class WaterCentrifugeTileEntity extends TileEntity implements ITickable{

	private ItemStack inv = ItemStack.EMPTY;
	private FluidStack water;
	private FluidStack dWater;
	private final int CAPACITY = 10_000;
	private final double TIP_POINT = .5D;
	private boolean neg;

	private final double[] motionData = new double[4];
	private final double[] physData = new double[] {912.5, 114.0625};

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(Math.abs(motionData[0]) >= TIP_POINT && (Math.signum(motionData[0]) == -1) == neg){
			neg = !neg;
			if(water != null && water.amount >= 100){
				if((water.amount -= 100) == 0){
					water = null;
				}
				dWater = new FluidStack(BlockDistilledWater.getDistilledWater(), Math.min(CAPACITY, 100 + (dWater == null ? 0 : dWater.amount)));
				inv = new ItemStack(ModItems.dustSalt, Math.min(64, 1 + inv.getCount()));
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
		if(!inv.isEmpty()){
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
		inv = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
	}

	private final IFluidHandler waterHandler = new WaterHandler();
	private final IFluidHandler dWaterHandler = new dWaterHandler();
	private final IFluidHandler masterHandler = new MasterHandler();
	private final IItemHandler saltHandler = new SaltHandler();
	private final IAxleHandler axleHandler = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)){
			return true;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
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
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing.getAxis() == Axis.X) != world.getBlockState(pos).getValue(Properties.ORIENT)){
			return (T) waterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing.getAxis() == Axis.X) == world.getBlockState(pos).getValue(Properties.ORIENT)){
			return (T) dWaterHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) saltHandler;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(cap, facing);
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
		public void resetAngle(){

		}

		@SideOnly(Side.CLIENT)
		@Override
		public double getAngle(){
			return 0;
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
			return slot == 0 ? inv : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || inv.isEmpty()){
				return ItemStack.EMPTY;
			}

			int cap = Math.min(amount, inv.getCount());

			if(!simulate){
				inv.shrink(cap);
			}

			return new ItemStack(ModItems.dustSalt, cap);
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}
}
