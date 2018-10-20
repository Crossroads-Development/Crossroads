package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;

import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class SteamTurbineTileEntity extends TileEntity implements ITickable, IInfoTE{

	private FluidStack steamContent;
	private FluidStack waterContent;
	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	private final double[] motionData = new double[4];

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Speed: " + MiscOp.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscOp.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscOp.betterRound(motionData[2], 3));
		chat.add("I: " + axleHandler.getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
	}

	@Override
	public void update(){
		if(world.isRemote){
			IAxleHandler gear = null;
			TileEntity te = world.getTileEntity(pos.offset(EnumFacing.UP));
			if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
			}
			completion = (float) (gear == null ? 0 : gear.getAngle());
			return;
		}

		if(steamContent != null){
			runMachine();
		}
	}
	
	private float completion;
	
	/**
	 * This uses the angle of the attached gear instead of calculating its own for a few reasons. It will always be attached when it should spin, and should always have the same angle as the attached gear (no point calculating).
	 */
	@SideOnly(Side.CLIENT)
	public float getCompletion(){
		return completion;
	}

	private void runMachine(){
		int limit = steamContent.amount / 100;
		limit = Math.min(limit, (CAPACITY - (waterContent == null ? 0 : waterContent.amount)) / 100);
		limit = Math.min(limit, LIMIT);
		if(limit != 0){
			steamContent.amount -= limit * 100;
			if(steamContent.amount <= 0){
				steamContent = null;
			}
			waterContent = new FluidStack(BlockDistilledWater.getDistilledWater(), (waterContent == null ? 0 : waterContent.amount) + (100 * limit));
			if(axleHandler.hasMaster){
				axleHandler.addEnergy(((double) limit) * .1D * EnergyConverters.degPerSteamBucket(false) / EnergyConverters.degPerJoule(false), true, true);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		steamContent = FluidStack.loadFluidStackFromNBT(nbt);
		waterContent = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("water"));
		
		for(int i = 0; i < 4; i++){
			motionData[i] = nbt.getDouble("motion" + i);
		}
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
		
		for(int i = 0; i < 4; i++){
			nbt.setDouble("motion" + i, motionData[i]);
		}
		
		return nbt;
	}

	private final IFluidHandler waterHandler = new WaterFluidHandler();
	private final IFluidHandler steamHandler = new SteamFluidHandler();
	private final IFluidHandler innerHandler = new InnerFluidHandler();
	private final AxleHandler axleHandler = new AxleHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return true;
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerHandler;
			}

			if(facing == EnumFacing.DOWN){
				return (T) steamHandler;
			}else if(facing != EnumFacing.UP){
				return (T) waterHandler;
			}
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private boolean hasMaster;

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
			hasMaster = true;
		}

		@Override
		public double getMoInertia(){
			return 80;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}

		@Override
		public void disconnect(){
			hasMaster = false;
		}
	}
	
	private class WaterFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(waterContent, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource != null && resource.getFluid() == BlockDistilledWater.getDistilledWater() && waterContent != null){
				int change = Math.min(waterContent.amount, resource.amount);

				if(doDrain){
					waterContent.amount -= change;
					if(waterContent.amount == 0){
						waterContent = null;
					}
				}

				return new FluidStack(BlockDistilledWater.getDistilledWater(), change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(waterContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(waterContent.amount, maxDrain);

			if(doDrain){
				waterContent.amount -= change;
				if(waterContent.amount == 0){
					waterContent = null;
				}
			}
			return new FluidStack(BlockDistilledWater.getDistilledWater(), change);
		}
	}

	private class SteamFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != BlockSteam.getSteam()){
				return 0;
			}
			int change = Math.min(CAPACITY - (steamContent == null ? 0 : steamContent.amount), resource.amount);
			if(doFill){
				steamContent = new FluidStack(BlockSteam.getSteam(), change + (steamContent == null ? 0 : steamContent.amount));
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

	private class InnerFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(waterContent, CAPACITY, false, true), new FluidTankProperties(steamContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != BlockSteam.getSteam()){
				return 0;
			}
			int change = Math.min(CAPACITY - (steamContent == null ? 0 : steamContent.amount), resource.amount);
			if(doFill){
				steamContent = new FluidStack(BlockSteam.getSteam(), change + (steamContent == null ? 0 : steamContent.amount));
			}
			return change;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource != null && resource.getFluid() == BlockDistilledWater.getDistilledWater() && waterContent != null){
				int change = Math.min(waterContent.amount, resource.amount);

				if(doDrain){
					waterContent.amount -= change;
					if(waterContent.amount == 0){
						waterContent = null;
					}
				}

				return new FluidStack(BlockDistilledWater.getDistilledWater(), change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(waterContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(waterContent.amount, maxDrain);

			if(doDrain){
				waterContent.amount -= change;
				if(waterContent.amount == 0){
					waterContent = null;
				}
			}
			return new FluidStack(BlockDistilledWater.getDistilledWater(), change);
		}
	}
}
