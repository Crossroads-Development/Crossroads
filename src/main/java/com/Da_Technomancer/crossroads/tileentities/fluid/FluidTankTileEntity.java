package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidTankTileEntity extends TileEntity{

	private FluidStack content = null;
	private final int CAPACITY = 20_000;

	private void fixState(){
		int i = content == null ? 0 : ((int) Math.ceil(15D * content.amount / CAPACITY));
		if(i != world.getBlockState(pos).getValue(Properties.REDSTONE)){
			world.setBlockState(pos, ModBlocks.fluidTank.getDefaultState().withProperty(Properties.REDSTONE, i < 0 ? 0 : i > 15 ? 15 : i));
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
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

	/*
	 * For setting the fluidstack on placement.
	 */
	public void setContent(FluidStack contentIn){
		content = contentIn;
		fixState();
	}

	private final IFluidHandler mainHandler = new MainHandler();
	private final IAdvancedRedstoneHandler redstoneHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) mainHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redstoneHandler;
		}
		
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(){
			return content == null ? 0 : 15D * (double) content.amount / (double) CAPACITY;
		}
	}
	
	private class MainHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (content == null || resource.isFluidEqual(content))){
				int amount = Math.min(resource.amount, CAPACITY - (content == null ? 0 : content.amount));

				if(doFill && amount != 0){
					content = new FluidStack(resource.getFluid(), amount + (content == null ? 0 : content.amount), resource.tag);
					fixState();
				}

				return amount;
			}

			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || content == null || resource.getFluid() != content.getFluid()){
				return null;
			}
			int amount = Math.min(resource.amount, content.amount);

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
				fixState();
			}

			return new FluidStack(resource.getFluid(), amount);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(maxDrain <= 0 || content == null){
				return null;
			}
			int amount = Math.min(maxDrain, content.amount);

			Fluid fluid = content.getFluid();

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
				fixState();
			}

			return new FluidStack(fluid, amount);
		}
	}
}
