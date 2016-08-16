package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveGear;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamTurbine;
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
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class SteamTurbineTileEntity extends TileEntity implements ITickable, IIntReceiver{

	private FluidStack steamContent;
	private FluidStack steamContentOut;
	private FluidStack waterContent;
	private static final int CAPACITY = 10_000;
	private int completion = 0;
	private int lastCompl = 0;
	private EnumFacing dir = EnumFacing.NORTH;
	private boolean toFace = true;
	private static final int LIMIT = 5;

	@Override
	public void update(){
		if(toFace){
			dir = worldObj.getBlockState(pos).getValue(SteamTurbine.FACING);
			toFace = false;
		}

		if(worldObj.isRemote){
			return;
		}

		if(lastCompl != completion){
			SendIntToClient msg = new SendIntToClient("prog", completion, this.getPos());
			ModPackets.network.sendToAllAround(msg, new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			lastCompl = completion;
		}

		if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) instanceof SteamTurbineTileEntity){
			steamOutHandler.drain(worldObj.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN).fill(steamContentOut, true), true);
		}

		if(steamContent != null){
			runMachine();
		}

	}

	@Override
	public void receiveInt(String context, int message){
		switch(context){
			case "prog":
				completion = message;
				break;
			default:
				return;
		}
	}

	public float getCompletion(){
		return .1F * ((float) completion);
	}

	public int getAngle(){
		switch(dir){
			case NORTH:
				return 180;
			case EAST:
				return 270;
			case WEST:
				return 90;
			default:
				return 0;

		}
	}

	private IRotaryHandler getGear(){
		int dis = 0;
		while(true){
			++dis;
			TileEntity te = worldObj.getTileEntity(pos.offset(EnumFacing.UP, dis));
			if(te != null && !(te instanceof ISlaveGear) && te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				return te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN);
			}

			if(!(te instanceof SteamTurbineTileEntity)){
				return null;
			}
		}
	}

	private void runMachine(){
		if(getGear() == null){
			return;
		}

		int limit = steamContent.amount / 100;
		limit = Math.min(limit, (CAPACITY - (waterContent == null ? 0 : waterContent.amount)) / 50);
		limit = Math.min(limit, (CAPACITY - (steamContentOut == null ? 0 : steamContentOut.amount)) / 50);
		limit = Math.min(limit, LIMIT);
		if(limit != 0){
			getGear().addEnergy(limit * .5D * .1D * EnergyConverters.DEG_PER_BUCKET_STEAM / EnergyConverters.DEG_PER_JOULE, true, true);
			steamContent.amount -= limit * 100;
			if(steamContent.amount <= 0){
				steamContent = null;
			}
			waterContent = new FluidStack(BlockDistilledWater.getDistilledWater(), (waterContent == null ? 0 : waterContent.amount) + (50 * limit));
			steamContentOut = new FluidStack(BlockSteam.getSteam(), (steamContentOut == null ? 0 : steamContentOut.amount) + (50 * limit));
			completion += limit;
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		steamContent = FluidStack.loadFluidStackFromNBT(nbt);

		waterContent = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("water"));

		steamContentOut = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("waste"));
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

		NBTTagCompound steamOutHolder = new NBTTagCompound();
		if(steamContentOut != null){
			steamContentOut.writeToNBT(steamOutHolder);
		}

		nbt.setTag("water", waterHolder);
		nbt.setTag("waste", steamOutHolder);

		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private final IFluidHandler waterHandler = new WaterFluidHandler();
	private final IFluidHandler steamHandler = new SteamFluidHandler();
	private final IFluidHandler steamOutHandler = new SteamOutFluidHandler();
	private final IFluidHandler innerHandler = new InnerFluidHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){

			if(facing == null){
				return (T) innerHandler;
			}

			if(facing == dir){
				return (T) waterHandler;
			}else if(facing == EnumFacing.DOWN){
				return (T) steamHandler;
			}else if(facing != EnumFacing.UP){
				return (T) steamOutHandler;
			}
		}

		return super.getCapability(capability, facing);
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

	private class SteamOutFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContentOut, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource != null && resource.getFluid() == BlockSteam.getSteam() && steamContentOut != null){
				int change = Math.min(steamContentOut.amount, resource.amount);

				if(doDrain){
					steamContentOut.amount -= change;
					if(steamContentOut.amount == 0){
						steamContentOut = null;
					}
				}

				return new FluidStack(BlockSteam.getSteam(), change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(steamContentOut == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(steamContentOut.amount, maxDrain);

			if(doDrain){
				steamContentOut.amount -= change;
				if(steamContentOut.amount == 0){
					steamContentOut = null;
				}
			}

			return new FluidStack(BlockSteam.getSteam(), change);
		}
	}

	private class InnerFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(waterContent, CAPACITY, false, true), new FluidTankProperties(steamContent, CAPACITY, true, false), new FluidTankProperties(steamContentOut, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
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
}
