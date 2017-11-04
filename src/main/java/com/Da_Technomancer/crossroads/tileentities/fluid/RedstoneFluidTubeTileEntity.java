package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			for(EnumFacing dir : EnumFacing.values()){
				TileEntity offsetTE = world.getTileEntity(pos.offset(dir));
				if(offsetTE != null && offsetTE.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())){
					transfer(offsetTE.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()));
				}
			}
		}
	}

	private void transfer(IFluidHandler handler){

		// the FluidTankProperties are completely ignored due to them being
		// unreliable.
		// Alternate methods of obtaining the information are used

		// False means either draining in not allowed or the tank is empty
		boolean canDrain = handler.drain(1, false) != null;

		if(!canDrain && content == null){
			return;
		}
		// False means either the tank is full or filling is disallowed with the
		// liquid in this pipe
		boolean canFill = handler.fill(content == null ? handler.drain(1, false) : content, false) != 0;

		if(!canDrain && !canFill){
			// if both are false, there is nothing to be done.
			return;
		}

		if(!canDrain){
			// content != null
			content.amount -= handler.fill(content, true);

			if(content.amount <= 0){
				content = null;
			}

			markDirty();

			//It's possible the connected machine does allow draining but was just empty. This checks for that. 
			if(handler.drain(1, false) != null){
				canDrain = true;
			}else{
				return;
			}
		}
		// content can = null

		// If this pipe and the tank are full, there is nothing to be done anyway
		if(!canFill && CAPACITY != (content == null ? 0 : content.amount)){
			if(content == null){
				content = handler.drain(CAPACITY, true);
			}else{
				FluidStack drained = handler.drain(new FluidStack(content.getFluid(), CAPACITY - content.amount), true);
				content.amount += drained == null ? 0 : drained.amount;
			}
			if(content != null && content.amount <= 0){
				content = null;
			}
			markDirty();
			if(handler.fill(content, false) == 0){
				return;
			}
		}

		// content can = null

		// KNOWN: canFill & canDrain tank & pipe, tank and pipe are not BOTH
		// full, tank and pipe are not BOTH empty, capacity and contents of
		// pipe.

		FluidStack fakeFullDrained = handler.drain(Integer.MAX_VALUE, false);
		long tankContent = fakeFullDrained == null ? 0 : fakeFullDrained.amount;
		long tankCapacity = tankContent + handler.fill(content == null ? new FluidStack(fakeFullDrained.getFluid(), Integer.MAX_VALUE) : new FluidStack(content.getFluid(), Integer.MAX_VALUE), false);

		long total = (content == null ? 0 : content.amount) + tankContent;

		Fluid fluid = content == null ? fakeFullDrained.getFluid() : content.getFluid();

		long targetOtherContent = Math.round(((double) total * tankCapacity) / ((double) (CAPACITY + tankCapacity)));
		int targetContent = (int) (total - targetOtherContent);

		content = null;

		if(fluid != null){
			if(targetOtherContent - tankContent >= 0){
				handler.fill(new FluidStack(fluid, (int) (targetOtherContent - tankContent)), true);
			}else{
				handler.drain((int) (tankContent - targetOtherContent), true);
			}

			if(targetContent > 0){
				content = new FluidStack(fluid, targetContent);
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

				if(doFill && change != 0){
					content = new FluidStack(resource.getFluid(), (content == null ? 0 : content.amount) + change);
					markDirty();
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

				if(doDrain && change != 0){
					content.amount -= change;
					if(content.amount == 0){
						content = null;
					}
					markDirty();
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

			if(doDrain && change != 0){
				content.amount -= change;
				if(content.amount == 0){
					content = null;
				}
				markDirty();
			}

			return new FluidStack(fluid, change);
		}
	}
}
