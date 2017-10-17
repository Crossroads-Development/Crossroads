package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArcaneReflectorTileEntity extends BeamRenderTE{
	
	public void resetFacing(){
		facing = -1;
	}
	
	private int facing = -1;
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
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
