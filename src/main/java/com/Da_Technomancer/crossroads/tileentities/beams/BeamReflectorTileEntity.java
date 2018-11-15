package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;

public class BeamReflectorTileEntity extends BeamRenderTE{

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		facing = -1;
	}

	private int facing = -1;
	
	@Override
	protected void doEmit(BeamUnit toEmit){
		if(beamer[facing].emit(toEmit, world)){
			refreshBeam(facing);
		}
		if(toEmit != null){
			prevMag[facing] = toEmit;
		}
	}

	@Override
	protected boolean[] inputSides(){
		if(facing == -1){
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex();
		}
		boolean[] out = {true, true, true, true, true, true};
		out[facing] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		if(facing == -1){
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex();
		}
		boolean[] out = {false, false, false, false, false, false};
		out[facing] = true;
		return out;
	}
} 
