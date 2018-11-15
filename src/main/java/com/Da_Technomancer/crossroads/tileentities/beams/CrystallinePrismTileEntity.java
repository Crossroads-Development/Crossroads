package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class CrystallinePrismTileEntity extends BeamRenderTE{

	private EnumFacing dir = null;

	private EnumFacing getDir(){
		if(dir == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.crystallinePrism){
				return EnumFacing.NORTH;
			}
			dir = state.getValue(Properties.HORIZ_FACING);
		}
		return dir;
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
	}

	@Override
	protected void doEmit(BeamUnit out){
		EnumFacing dir = getDir();
		//Energy
		if(beamer[dir.getIndex()].emit(out == null || out.getEnergy() == 0 ? null : out.mult(1, 0, 0, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Potential
		if(beamer[dir.getIndex()].emit(out == null || out.getPotential() == 0 ? null : out.mult(0, 1, 0, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Stability
		if(beamer[dir.getIndex()].emit(out == null || out.getStability() == 0 ? null : out.mult(0, 0, 1, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Void
		if(beamer[dir.getIndex()].emit(out == null || out.getVoid() == 0 ? null : out.mult(0, 0, 0, 1, false), world)){
			refreshBeam(dir.getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {true, true, false, false, false, false};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}
} 
