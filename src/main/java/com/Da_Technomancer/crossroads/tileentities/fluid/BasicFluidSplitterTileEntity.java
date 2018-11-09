package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class BasicFluidSplitterTileEntity extends ModuleTE{

	private static final int CAPACITY = 10_000;

	public BasicFluidSplitterTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, CAPACITY, false, true);//Bottom
		fluidProps[1] = new TankProperty(1, CAPACITY, false, true);//Top
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return side == null || side.getAxis() != EnumFacing.Axis.Y ? (T) inHandler : side == EnumFacing.UP ? (T) upHandler : (T) downHandler;
		}
		
		return super.getCapability(cap, side);
	}

	private final FluidHandler upHandler = new FluidHandler(0);
	private final FluidHandler downHandler = new FluidHandler(1);
	private final InHandler inHandler = new InHandler();
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("went_up", lastWentUp);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		lastWentUp = nbt.getBoolean("went_up");
	}
	
	private boolean lastWentUp = false;
	
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
			
			int accepted = Math.max(0, Math.min(resource.amount, Math.min(fluids[0] != null && !fluids[0].isFluidEqual(resource) ? 0 : (2 * (CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount))) + (lastWentUp ? -(resource.amount % 2) : resource.amount % 2), fluids[1] != null && !fluids[1].isFluidEqual(resource) ? 0 : (2 * (CAPACITY - (fluids[1] == null ? 0 : fluids[1].amount))) + (lastWentUp ? resource.amount % 2 : -(resource.amount % 2)))));
			
			int goUp = (accepted / 2) + (lastWentUp ? 0 : accepted % 2);
			int goDown = accepted - goUp;
			
			if(doFill && accepted != 0){
				fluids[0] = new FluidStack(resource.getFluid(), goDown + (fluids[0] == null ? 0 : fluids[0].amount), resource.tag);
				fluids[1] = new FluidStack(resource.getFluid(), goUp + (fluids[1] == null ? 0 : fluids[1].amount), resource.tag);
				lastWentUp = (accepted % 2 == 0) == lastWentUp;
				
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
