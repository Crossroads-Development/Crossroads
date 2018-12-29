package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class FluxStabilizerElectricTileEntity extends FluxTE{

	private int fe = 0;
	private static final int CAPACITY = 10_000;

	@Override
	public void update(){
		super.update();

		if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			flux = Math.max(0, flux - Math.min(8, fe / FluxUtil.FE_PER_FLUX));
			if(fe != 0){
				markDirty();
			}
			fe = Math.max(0, fe - 8 * FluxUtil.FE_PER_FLUX);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	@Override public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		fe = nbt.getInteger("fe");
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return false;
	}

	@Override
	public int canAccept(){
		return getCapacity() - flux;
	}

	@Override
	public boolean isFluxReceiver(){
		return true;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityEnergy.ENERGY){
			return (T) feHandler;
		}
		return super.getCapability(capability, facing);
	}

	private final EnergyHandler feHandler = new EnergyHandler();

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int received = Math.min(maxReceive, CAPACITY - fe);
			if(!simulate && received != 0){
				fe += received;
				markDirty();
			}
			return received;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}
}
