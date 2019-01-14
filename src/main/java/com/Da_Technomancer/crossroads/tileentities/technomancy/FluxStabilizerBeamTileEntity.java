package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class FluxStabilizerBeamTileEntity extends AbstractFluxStabilizerTE{

	private int stability = 0;

	public FluxStabilizerBeamTileEntity(){
		super();
	}

	public FluxStabilizerBeamTileEntity(boolean crystal){
		super(crystal);
	}

	@Override
	public void update(){
		super.update();

		if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			stability = Math.min(stability, FluxUtil.getStabilizerLimit(crystal));
			if(clientRunning ^ (stability != 0 && flux != 0)){
				clientRunning = !clientRunning;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 4, clientRunning ? 1L : 0L, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
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
