package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class RedstoneFluidTubeTileEntity extends TileEntity implements ITickable{

	private final int CAPACITY = 2000;
	private FluidStack content = null;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			for(EnumFacing dir : EnumFacing.values()){
				if(world.getTileEntity(pos.offset(dir)) != null && world.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())){
					transfer(world.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()));
				}
			}
		}
	}

	private void transfer(IFluidHandler handler){

		// the FluidTankProperties are completely ignored due to them being
		// unreliable.
		// Alternate methods of obtaining the information are used

		// False means either draining in not allowed or the tank is empty
		boolean canDrain = handler.drain(10, false) != null;
		// False means either the tank is full or filling is disallowed with the
		// liquid in this pipe
		boolean canFill = handler.fill(content, false) != 0;

		if(!canDrain && !canFill){
			return;
		}
		// if both are false, there is nothing to be done.

		// content cannot = null
		if(!canDrain){
			content.amount -= handler.fill(content, true);

			if(content.amount == 0){
				content = null;
			}

			canDrain = handler.drain(10, false) != null;
		}
		// content can = null

		// If this pipe and the tank are full, there is nothing to be done
		// anyways
		if(!canFill && CAPACITY != (content == null ? 0 : content.amount)){
			if(content == null){
				content = handler.drain(CAPACITY, true);
			}else{
				content.amount += handler.drain(new FluidStack(content.getFluid(), CAPACITY - content.amount), false) == null ? 0 : handler.drain(new FluidStack(content.getFluid(), CAPACITY - content.amount), true).amount;
			}

			canFill = handler.fill(content, false) != 0;
		}

		// content can = null

		if(canFill && canDrain){

			// KNOWN: canFill & canDrain tank & pipe, tank and pipe are not BOTH
			// full, tank and pipe are not BOTH empty, capacity and contents of
			// pipe.

			int tankContent = handler.drain(Integer.MAX_VALUE, false) == null ? 0 : handler.drain(Integer.MAX_VALUE, false).amount;
			int tankCapacity = tankContent + handler.fill(content == null ? new FluidStack(handler.drain(1, false).getFluid(), Integer.MAX_VALUE) : new FluidStack(content.getFluid(), Integer.MAX_VALUE), false);

			int total = (content == null ? 0 : content.amount) + tankContent;

			Fluid fluid = content == null ? handler.drain(1, false).getFluid() : content.getFluid();

			double contentTwoDouble = total * tankCapacity / ((double) (CAPACITY + tankCapacity));

			int contentTwo = (int) Math.round(contentTwoDouble);
			int contentOne = total - contentTwo;

			if(tankContent != 0){
				handler.drain(tankContent, true);
			}

			content = null;

			if(fluid != null){
				if(contentTwo != 0){
					handler.fill(new FluidStack(fluid, contentTwo), true);
				}
				if(contentOne != 0){
					content = new FluidStack(fluid, contentOne);
				}
			}

		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}

		return nbt;
	}

	private final IFluidHandler mainHandler = new MainFluidHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return (T) mainHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL);
		}
		return super.hasCapability(capability, facing);
	}

	private class MainFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (content == null || resource.isFluidEqual(content))){
				int change = Math.min(CAPACITY - (content == null ? 0 : content.amount), resource.amount);

				if(doFill){
					content = new FluidStack(resource.getFluid(), (content == null ? 0 : content.amount) + change);
				}

				return change;
			}else{
				return 0;
			}
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
