package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class ClockworkStabilizerTileEntity extends BeamRenderTE{

	private static final double RATE = 0.25D;
	private BeamUnitStorage storage = new BeamUnitStorage();

	public ClockworkStabilizerTileEntity(){
		super();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		storage.writeToNBT("stab_mag", nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		storage = BeamUnitStorage.readFromNBT("stab_mag", nbt);
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		storage.addBeam(toEmit);

		//Enforce LIMIT
		if(storage.getOutput() != null && storage.getOutput().getPower() > getLimit()){
			BeamUnit stored = storage.getOutput();
			storage.clear();
			storage.addBeam(stored.mult((double) getLimit() / (double) stored.getPower(), false));
		}

		EnumFacing dir = world.getBlockState(pos).getValue(EssentialsProperties.FACING);

		if(!storage.isEmpty()){
			BeamUnit mag = storage.getOutput().mult(RATE, true);
			storage.subtractBeam(mag);
			if(beamer[dir.getIndex()].emit(mag.getPower() == 0 ? null : mag, world)){
				refreshBeam(dir.getIndex());
			}
		}else if(beamer[dir.getIndex()].emit(null, world)){
			refreshBeam(dir.getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = true;
		return out;
	}
} 
