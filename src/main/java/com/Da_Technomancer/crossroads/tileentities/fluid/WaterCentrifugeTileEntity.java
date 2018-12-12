package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class WaterCentrifugeTileEntity extends InventoryTE{

	private static final double TIP_POINT = .5D;
	private boolean neg;

	public WaterCentrifugeTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 10_000, true, false, (Fluid f) -> f == FluidRegistry.WATER);
		fluidProps[1] = new TankProperty(1, 10_000, false, true, null);
	}

	public boolean isNeg(){
		return neg;
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useRotary(){
		return true;
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		if(Math.abs(motData[0]) >= TIP_POINT && (Math.signum(motData[0]) == -1) == neg){
			neg = !neg;
			if(fluids[0] != null && fluids[0].amount >= 100){
				if((fluids[0].amount -= 100) == 0){
					fluids[0] = null;
				}
				fluids[1] = new FluidStack(BlockDistilledWater.getDistilledWater(), Math.min(fluidProps[1].getCapacity(), 100 + (fluids[1] == null ? 0 : fluids[1].amount)));
				inventory[0] = new ItemStack(ModItems.dustSalt, Math.min(64, 1 + inventory[0].getCount()));
				markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("neg", neg);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		neg = nbt.getBoolean("neg");
	}

	private final IFluidHandler waterHandler = new FluidHandler(0);
	private final IFluidHandler dWaterHandler = new FluidHandler(1);
	private final IFluidHandler masterHandler = new FluidHandler(-1);
	private final IItemHandler saltHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == null){
			return (T) masterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != world.getBlockState(pos).getValue(Properties.HORIZ_AXIS)){
			return (T) waterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZ_AXIS)){
			return (T) dWaterHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) saltHandler;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && facing == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(cap, facing);
	}

	@Override
	public double getMoInertia(){
		return 115;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.water_centrifuge";
	}
}
