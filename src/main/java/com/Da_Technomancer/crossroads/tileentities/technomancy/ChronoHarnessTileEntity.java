package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ChronoHarnessTileEntity extends FluxTE{

	public static final int POWER = FluxUtil.getFePerFlux(true) / FluxUtil.FLUX_TIME;
	private static final int CAPACITY = 10_000;

	private int fe = 0;
	private float partialFlux = 0;

	private boolean hasRedstone(){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.chronoHarness){
			return state.getValue(EssentialsProperties.REDSTONE_BOOL);
		}
		invalidate();
		return true;
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && fe + POWER <= CAPACITY && !hasRedstone()){
			fe += POWER;
			markDirty();
			partialFlux += (float) POWER / (float) FluxUtil.getFePerFlux(false);
			if(partialFlux >= 1F){
				partialFlux -= 1F;
				addFlux(1);
			}
		}

		//Transer FE to a machine above
		if(!world.isRemote && fe != 0){
			TileEntity neighbor = world.getTileEntity(pos.offset(EnumFacing.UP));
			IEnergyStorage storage;
			if(neighbor != null && (storage = neighbor.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN)) != null){
				if(storage.canReceive()){
					fe -= storage.receiveEnergy(energyHandler.getEnergyStored(), false);
					markDirty();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) energyHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("fe", fe);
		nbt.setFloat("partial_flux", partialFlux);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		fe = nbt.getInteger("fe");
		partialFlux = nbt.getFloat("partial_flux");
	}

	@Override
	public boolean isFluxEmitter(){
		return true;
	}

	@Override
	public boolean isFluxReceiver(){
		return false;
	}

	private final EnergyHandler energyHandler = new EnergyHandler();

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int extracted = Math.min(maxExtract, fe);
			if(!simulate && extracted > 0){
				fe -= extracted;
				markDirty();
			}
			return extracted;
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
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}
}
