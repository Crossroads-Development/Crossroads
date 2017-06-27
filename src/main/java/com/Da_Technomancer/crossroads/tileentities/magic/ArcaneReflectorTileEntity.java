package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.util.EnumFacing;

public class ArcaneReflectorTileEntity extends BeamRenderTE{

	private EnumFacing facing;
	
	@Override
	protected void doEmit(MagicUnit toEmit){
		beamer[facing.getIndex()].emit(toEmit, world);
	}

	@Override
	protected boolean[] inputSides(){
		if(facing == null){
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}
		boolean[] out = {true, true, true, true, true, true};
		out[facing.getIndex()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		if(facing == null){
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}
		boolean[] out = {false, false, false, false, false, false};
		out[facing.getIndex()] = true;
		return out;
	}
} 
