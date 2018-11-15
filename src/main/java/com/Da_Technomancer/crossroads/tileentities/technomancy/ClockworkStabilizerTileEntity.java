package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class ClockworkStabilizerTileEntity extends BeamRenderTE{

	private static final double RATE = 0.25D;
	private BeamUnitStorage storage = new BeamUnitStorage();
	private EnumFacing dir = null;

	private EnumFacing getDir(){
		if(dir == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.clockworkStabilizer){
				return EnumFacing.NORTH;
			}
			dir = state.getValue(EssentialsProperties.FACING);
		}
		return dir;
	}


	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
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
		if(storage.getPower() > getLimit()){
			BeamUnit stored = storage.getOutput();
			storage.clear();
			storage.addBeam(stored.mult((double) getLimit() / (double) stored.getPower(), true));
		}

		EnumFacing dir = getDir();

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
		out[getDir().getIndex()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getDir().getIndex()] = true;
		return out;
	}
} 
