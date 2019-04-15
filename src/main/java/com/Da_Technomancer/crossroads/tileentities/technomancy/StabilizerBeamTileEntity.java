package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StabilizerBeamTileEntity extends AbstractStabilizerTileEntity{

	private int stability = 0;

	public StabilizerBeamTileEntity(){
		super();
	}

	@Override
	protected int drainFuel(){
		int drained = Math.min(8, stability);
		stability -= drained;
		if(drained != 0){
			markDirty();
		}
		return drained;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("stability", stability);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stability = nbt.getInteger("stability");
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
			if(mag != null){
				EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
				if(align == EnumBeamAlignments.STABILITY){
					stability += mag.getPower();
					markDirty();
				}else if(align == EnumBeamAlignments.TIME && mag.getVoid() != 0){
					//If FLUX beam is added, create entropy
					stability = 0;
					markDirty();
					EntropySavedData.addEntropy(world, mag.getPower() * 4);
				}
			}
		}
	}
}
