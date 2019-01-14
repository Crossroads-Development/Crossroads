package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class FluxStabilizerElectricTileEntity extends AbstractFluxStabilizerTE{

	private int fe = 0;
	private static final int CAPACITY = 10_000;

	public FluxStabilizerElectricTileEntity(){
		super();
	}

	public FluxStabilizerElectricTileEntity(boolean crystal){
		super(crystal);
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			if(clientRunning ^ (fe != 0 && flux != 0)){
				clientRunning = !clientRunning;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 4, clientRunning ? 1L : 0L, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			int cap = FluxUtil.getStabilizerLimit(crystal);
			flux = Math.max(0, flux - Math.min(cap, fe / FluxUtil.getFePerFlux(false)));
			if(fe != 0){
				markDirty();
			}
			fe = Math.max(0, fe - cap * FluxUtil.getFePerFlux(false));
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
