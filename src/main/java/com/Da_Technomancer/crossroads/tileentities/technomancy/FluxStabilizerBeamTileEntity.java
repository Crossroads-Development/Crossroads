package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class FluxStabilizerBeamTileEntity extends FluxTE{

	private int stability = 0;

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			stability = Math.min(stability, 8);
			flux = Math.max(0, flux - stability);
			stability = 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("stability", stability);
		return nbt;
	}

	@Override public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stability = nbt.getInteger("stability");
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
		if(capability == Capabilities.BEAM_CAPABILITY){
			return (T) beamHandler;
		}
		return super.getCapability(capability, facing);
	}

	private final BeamHandler beamHandler = new BeamHandler();

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(@Nullable BeamUnit mag){
			if(mag != null && EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.STABILITY){
				stability += mag.getPower();
				markDirty();
			}
		}
	}
}
