package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidSplitterTileEntity extends ModuleTE{

	public FluidSplitterTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, CAPACITY, false, true);//Bottom
		fluidProps[1] = new TankProperty(1, CAPACITY, false, true);//Top
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	public int redstone;
	private static final int CAPACITY = 10_000;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return side == null || side.getAxis() != EnumFacing.Axis.Y ? (T) inHandler : side == EnumFacing.UP ? (T) upHandler : (T) downHandler;
		}

		return super.getCapability(cap, side);
	}

	private final FluidHandler downHandler = new FluidHandler(0);
	private final FluidHandler upHandler = new FluidHandler(1);
	private final InHandler inHandler = new InHandler();

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		nbt.setInteger("transfered", transfered);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
		transfered = nbt.getInteger("transfered");
	}

	private int transfered = 0;

	private class InHandler implements IFluidHandler{

		private final TankProperty[] properties = new TankProperty[] {new TankProperty(0, CAPACITY, true, false), new TankProperty(1, CAPACITY, true, false)};

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return properties;
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null){
				return 0;
			}

			int accepted = Math.max(0, Math.min(resource.amount, redstone == 0 ? fluids[1] != null && !fluids[1].isFluidEqual(resource) ? 0 : CAPACITY - (fluids[1] == null ? 0 : fluids[1].amount) : redstone == 15 ? fluids[0] != null && !fluids[0].isFluidEqual(resource) ? 0 : CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount) : Math.min(fluids[0] != null && !fluids[0].isFluidEqual(resource) ? 0 : ((15 * (CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount))) / redstone), fluids[1] != null && !fluids[1].isFluidEqual(resource) ? 0 : ((15 * (CAPACITY - (fluids[1] == null ? 0 : fluids[1].amount))) / (15 - redstone)))));
			
			int goDown = (redstone * (accepted / 15)) + (transfered >= redstone ? 0 : Math.min(redstone - transfered, accepted % 15)) + Math.max(0, Math.min(redstone, (accepted % 15) + transfered - 15));
			int goUp = accepted - goDown;

			if(doFill && accepted != 0){
				fluids[0] = new FluidStack(resource.getFluid(), goDown + (fluids[0] == null ? 0 : fluids[0].amount), resource.tag);
				fluids[1] = new FluidStack(resource.getFluid(), goUp + (fluids[1] == null ? 0 : fluids[1].amount), resource.tag);
				transfered += accepted % 15;
				transfered %= 15;
			}

			return accepted;
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
