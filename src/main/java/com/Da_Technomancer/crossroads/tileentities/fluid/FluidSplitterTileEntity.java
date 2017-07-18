package com.Da_Technomancer.crossroads.tileentities.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidSplitterTileEntity extends TileEntity{

	public int redstone;
	private static final int CAPACITY = 10_000;

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return side == null || side.getAxis() != EnumFacing.Axis.Y ? (T) inHandler : side == EnumFacing.UP ? (T) upHandler : (T) downHandler;
		}

		return super.getCapability(cap, side);
	}

	private final OutHandler downHandler = new OutHandler(true);
	private final OutHandler upHandler = new OutHandler(false);
	private final InHandler inHandler = new InHandler();

	private FluidStack upFluid;
	private FluidStack downFluid;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		if(upFluid != null){
			nbt.setTag("upFluid", upFluid.writeToNBT(new NBTTagCompound()));
		}
		if(downFluid != null){
			nbt.setTag("downFluid", downFluid.writeToNBT(new NBTTagCompound()));
		}
		nbt.setInteger("transfered", transfered);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
		upFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("upFluid"));
		downFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("downFluid"));
		transfered = nbt.getInteger("transfered");
	}

	private class OutHandler implements IFluidHandler{

		private final boolean down;

		private OutHandler(boolean down){
			this.down = down;
		}

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(down ? downFluid : upFluid, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			FluidStack workingWith = down ? downFluid : upFluid;
			if(workingWith == null || resource == null || !workingWith.isFluidEqual(resource)){
				return null;
			}
			int drained = Math.min(resource.amount, workingWith.amount);

			if(doDrain){
				workingWith.amount -= drained;
				if(workingWith.amount <= 0){
					if(down){
						downFluid = null;
					}else{
						upFluid = null;
					}
				}
			}

			return new FluidStack(resource.getFluid(), drained, resource.tag);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			FluidStack workingWith = down ? downFluid : upFluid;
			if(workingWith == null){
				return null;
			}
			int drained = Math.min(maxDrain, workingWith.amount);
			FluidStack out = new FluidStack(workingWith.getFluid(), drained, workingWith.tag);

			if(doDrain){
				workingWith.amount -= drained;
				if(workingWith.amount <= 0){
					if(down){
						downFluid = null;
					}else{
						upFluid = null;
					}
				}
			}

			return out;
		}
	}

	private int transfered = 0;

	private class InHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(downFluid, CAPACITY, true, false), new FluidTankProperties(upFluid, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null){
				return 0;
			}

			int accepted = Math.max(0, Math.min(resource.amount, Math.min(downFluid != null && !downFluid.isFluidEqual(resource) ? 0 : ((15 * (CAPACITY - (downFluid == null ? 0 : downFluid.amount))) / redstone), upFluid != null && !upFluid.isFluidEqual(resource) ? 0 : ((15 * (CAPACITY - (upFluid == null ? 0 : upFluid.amount))) / (15 - redstone)))));
			
			int goDown = (redstone * (accepted / 15)) + (transfered >= redstone ? 0 : Math.min(redstone - transfered, accepted % 15)) + Math.max(0, Math.min(redstone, (accepted % 15) + transfered - 15));
			int goUp = accepted - goDown;

			if(doFill && accepted != 0){
				downFluid = new FluidStack(resource.getFluid(), goDown + (downFluid == null ? 0 : downFluid.amount), resource.tag);
				upFluid = new FluidStack(resource.getFluid(), goUp + (upFluid == null ? 0 : upFluid.amount), resource.tag);
				transfered += accepted % 15;;
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
