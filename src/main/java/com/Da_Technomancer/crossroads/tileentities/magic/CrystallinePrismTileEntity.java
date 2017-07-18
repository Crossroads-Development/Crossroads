package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.util.EnumFacing;

public class CrystallinePrismTileEntity extends BeamRenderTE{

	@Override
	protected void doEmit(MagicUnit out){
		EnumFacing dir = world.getBlockState(pos).getValue(Properties.FACING);
		beamer[dir.rotateYCCW().getIndex()].emit(out == null || out.getEnergy() == 0 ? null : out.mult(1, 0, 0, 0, false), world);
		beamer[dir.getIndex()].emit(out == null || out.getPotential() == 0 ? null : out.mult(0, 1, 0, 0, false), world);
		beamer[dir.rotateY().getIndex()].emit(out == null || out.getStability() == 0 ? null : out.mult(0, 0, 1, 0, false), world);

	}

	@Override
	protected boolean[] inputSides(){
		EnumFacing dir = world.getBlockState(pos).getValue(Properties.FACING);
		boolean[] out = new boolean[6];
		out[dir.getOpposite().getIndex()] = true;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		EnumFacing dir = world.getBlockState(pos).getValue(Properties.FACING);
		boolean[] out = new boolean[6];
		out[dir.getIndex()] = true;
		out[dir.rotateY().getIndex()] = true;
		out[dir.rotateYCCW().getIndex()] = true;
		return out;
	}
} 
