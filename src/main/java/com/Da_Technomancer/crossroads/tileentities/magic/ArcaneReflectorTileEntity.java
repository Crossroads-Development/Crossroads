package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

public class ArcaneReflectorTileEntity extends BeamRenderTE{

	private int facing = -1;
	
	@Override
	protected void doEmit(MagicUnit toEmit){
		beamer[facing].emit(toEmit, world);
	}

	@Override
	protected boolean[] inputSides(){
		if(facing == -1){
			facing = world.getBlockState(pos).getValue(Properties.FACING).getIndex();
		}
		boolean[] out = {true, true, true, true, true, true};
		out[facing] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		if(facing == -1){
			facing = world.getBlockState(pos).getValue(Properties.FACING).getIndex();
		}
		boolean[] out = {false, false, false, false, false, false};
		out[facing] = true;
		return out;
	}
} 
