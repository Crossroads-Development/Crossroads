package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class RadiatorTileEntity extends InventoryTE{

	public RadiatorTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, 10_000, true, false, (Fluid f) -> f == BlockSteam.getSteam());
		fluidProps[1] = new TankProperty(1, 10_000, false, true);
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useHeat(){
		return true;
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && fluids[0] != null && fluids[0].amount >= 100 && (fluids[1] == null || fluidProps[1].getCapacity() - fluids[1].amount >= 100)){
			temp += .1D * EnergyConverters.degPerSteamBucket(false);
			fluids[1] = new FluidStack(BlockDistilledWater.getDistilledWater(), 100 + (fluids[1] == null ? 0 : fluids[1].amount));
			fluids[0].amount -= 100;
			if(fluids[0].amount <= 0){
				fluids[0] = null;
			}
			markDirty();
		}
	}

	private final FluidHandler steamHandler = new FluidHandler(0);
	private final FluidHandler waterHandler = new FluidHandler(1);
	private final FluidHandler innerHandler = new FluidHandler(-1);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){

		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null){
				return (T) innerHandler;
			}

			if(side == EnumFacing.UP){
				return (T) waterHandler;
			}

			if(side == EnumFacing.DOWN){
				return (T) steamHandler;
			}
		}

		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && side != EnumFacing.UP && side != EnumFacing.DOWN){
			return (T) heatHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.radiator";
	}
}
