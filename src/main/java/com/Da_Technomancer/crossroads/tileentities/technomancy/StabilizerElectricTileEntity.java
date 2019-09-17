package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class StabilizerElectricTileEntity extends AbstractStabilizerTileEntity{

	private int fe = 0;
	private static final int CAPACITY = 10_000;

	public StabilizerElectricTileEntity(){
		super();
	}

	@Override
	protected int drainFuel(){
		int drained = Math.min(8 * FluxUtil.getFePerFlux(false), fe);
		fe -= drained;
		if(drained != 0){
			markDirty();
		}
		return drained / FluxUtil.getFePerFlux(false);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
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
