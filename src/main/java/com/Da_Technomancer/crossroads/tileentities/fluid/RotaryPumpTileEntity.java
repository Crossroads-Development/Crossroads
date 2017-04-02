package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;

import net.minecraft.block.BlockLiquid;
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

public class RotaryPumpTileEntity extends TileEntity implements ITickable, IIntReceiver{

	private final int REQUIRED = 200;
	private int progress = 0;
	private int lastProgress = 0;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			//If anyone knows a builtin way to simplify this if statement, be my guest. It's so long it scares me...
			if(FluidRegistry.lookupFluidForBlock(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock()) != null && (world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockFluidClassic && ((BlockFluidClassic) world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock()).isSourceBlock(world, pos.offset(EnumFacing.DOWN)) || world.getBlockState(pos.offset(EnumFacing.DOWN)).getValue(BlockLiquid.LEVEL) == 0) && (content == null || (CAPACITY - content.amount >= 1000 && content.getFluid() == FluidRegistry.lookupFluidForBlock(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock())))){
				IAxleHandler te = world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);

				double holder = MiscOp.findEfficiency(te.getMotionData()[0], .2D, 8) * Math.abs(te.getMotionData()[1]);
				te.addEnergy(-holder, false, false);
				progress += Math.round(holder);
			}else{
				progress = 0;
			}
		}

		if(progress >= REQUIRED){
			progress = 0;
			content = new FluidStack(FluidRegistry.lookupFluidForBlock(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock()), 1000 + (content == null ? 0 : content.amount));
			world.setBlockToAir(pos.offset(EnumFacing.DOWN));
		}

		if(lastProgress != progress){
			SendIntToClient msg = new SendIntToClient("prog", progress, this.getPos());
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			lastProgress = progress;
		}
	}

	private final int CAPACITY = 3000;
	private FluidStack content = null;

	public float getCompletion(){
		return ((float) progress) / ((float) REQUIRED);
	}

	@Override
	public void receiveInt(String context, int message, EntityPlayerMP player){
		switch(context){
			case "prog":
				progress = message;
				break;
			default:
				return;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
		progress = nbt.getInteger("prog");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		nbt.setInteger("prog", progress);

		return nbt;
	}

	private final IFluidHandler pumpedHandler = new PumpedFluidHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) pumpedHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
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
