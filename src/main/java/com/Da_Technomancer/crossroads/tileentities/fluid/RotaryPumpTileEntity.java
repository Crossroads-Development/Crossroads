package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RotaryPumpTileEntity extends TileEntity implements ITickable, IIntReceiver, IInfoTE{

	private static final double REQUIRED = 50;
	private double progress = 0;
	private int lastProgress = 0;

	private final double[] motionData = new double[4];

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Speed: " + MiscUtil.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscUtil.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscUtil.betterRound(motionData[2], 3));
		chat.add("I: " + axleHandler.getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		IBlockState fluidBlockstate = world.getBlockState(pos.offset(EnumFacing.DOWN));
		Block fluidBlock = fluidBlockstate.getBlock();
		//If anyone knows a builtin way to simplify this if statement, be my guest. It's so long it scares me...
		if(FluidRegistry.lookupFluidForBlock(fluidBlock) != null && (fluidBlock instanceof BlockFluidClassic && ((BlockFluidClassic) fluidBlock).isSourceBlock(world, pos.offset(EnumFacing.DOWN)) || fluidBlockstate.getValue(BlockLiquid.LEVEL) == 0) && (content == null || (CAPACITY - content.amount >= 1000 && content.getFluid() == FluidRegistry.lookupFluidForBlock(fluidBlock)))){
			double holder = motionData[1] < 0 ? 0 : Math.min(motionData[1], REQUIRED - progress);
			motionData[1] -= holder;
			progress += holder;
		}else{
			progress = 0;
		}

		if(progress >= REQUIRED){
			progress = 0;
			content = new FluidStack(FluidRegistry.lookupFluidForBlock(fluidBlock), 1000 + (content == null ? 0 : content.amount));
			world.setBlockToAir(pos.offset(EnumFacing.DOWN));
		}

		if(lastProgress != (int) progress){
			SendIntToClient msg = new SendIntToClient(0, (int) progress, pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			lastProgress = (int) progress;
		}
	}

	private static final int CAPACITY = 3000;
	private FluidStack content = null;

	public float getCompletion(){
		return ((float) progress) / ((float) REQUIRED);
	}

	@Override
	public void receiveInt(int identifier, int message, EntityPlayerMP player){
		if(identifier == 0){
			progress = message;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
		progress = nbt.getDouble("prog");
		for(int i = 0; i < 4; i++){
			motionData[i] = nbt.getDouble("motion" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		nbt.setDouble("prog", progress);
		for(int i = 0; i < 4; i++){
			nbt.setDouble("motion" + i, motionData[i]);
		}
		return nbt;
	}

	private final PumpedFluidHandler pumpedHandler = new PumpedFluidHandler();
	private final AxleHandler axleHandler = new AxleHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) pumpedHandler;
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return true;
		}
		return super.hasCapability(capability, facing);
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
		public double getMoInertia(){
			return 80;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
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

		@Override
		public boolean shouldManageAngle(){
			return false;
		}
	}

	private class PumpedFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(content, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.isFluidEqual(content)){
				int change = Math.min(content.amount, resource.amount);
				Fluid fluid = content.getFluid();

				if(doDrain){
					content.amount -= change;
					if(content.amount == 0){
						content = null;
					}
				}

				return new FluidStack(fluid, change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(content == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(content.amount, maxDrain);
			Fluid fluid = content.getFluid();

			if(doDrain){
				content.amount -= change;
				if(content.amount == 0){
					content = null;
				}
			}

			return new FluidStack(fluid, change);
		}
	}
}
