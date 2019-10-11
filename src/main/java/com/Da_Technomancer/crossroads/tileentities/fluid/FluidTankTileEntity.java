package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class FluidTankTileEntity extends InventoryTE{

	public static final int CAPACITY = 16_000;

	public FluidTankTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, CAPACITY, true, true);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	public int getRedstone(){
		return Math.min(15, Math.max(0, fluids[0] == null ? 0 : ((int) Math.ceil(15D * fluids[0].amount / CAPACITY))));
	}

	/*
	 * For setting the fluidstack on placement.
	 */
	public void setContent(FluidStack contentsIn){
		fluids[0] = contentsIn;
		markDirty();
	}

	private final IFluidHandler mainHandler = new FluidHandler(0);
	private final IAdvancedRedstoneHandler redstoneHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) mainHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redstoneHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.fluid_tank";
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return read ? fluids[0] == null ? 0 : 15D * (double) fluids[0].amount / (double) CAPACITY : 0;
		}
	}
}
